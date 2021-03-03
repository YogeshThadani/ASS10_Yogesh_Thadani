package javadb;

import java.util.*;
import java.sql.*;
import java.io.*;

public class ContactService {
    void addContact(Contact contact,List<Contact> contacts)
    {
        Scanner scan = new Scanner(System.in);
        int n;
        List<String> c = new ArrayList<>();
        int i;
        
        System.out.println("Enter contact id : ");
        contact.setContactId(scan.nextInt());
        System.out.println("Enter contact name : ");
        scan.nextLine();
        contact.setContactName(scan.nextLine());
        System.out.println("Enter contact email : ");
        contact.setEmail(scan.nextLine());
        System.out.println("How many contact numbers to enter :");
        n = scan.nextInt();
        System.out.println("Enter the "+n +" contact number s : ");
        scan.nextLine();
        for(i=0; i<n; i++)
            c.add(scan.nextLine());
        contact.setContactNumber(c);
        
        //add to list
        contacts.add(contact);
    }//addContact

    void removeContact(Contact contact, List<Contact> contacts) throws ContactNotFoundException
    {
        for(Contact c : contacts)
            if(c.getContactId() == contact.getContactId())
            {
                contacts.remove(c);
                System.out.println("Contact Removed!!");
                return;
            }
        
        throw new ContactNotFoundException("Contact Not Found!!");
    }
    
    Contact searchContactByName(String name, List<Contact> contacts) throws ContactNotFoundException
    {
        Contact temp=null;
        for(Contact c : contacts)
        {
            if(c.getContactName().equalsIgnoreCase(name))
            {
                temp = c;
                break;
            }
        }
        
        if(temp == null)
            throw new ContactNotFoundException("No contact with name : "+name);
        
        return temp;
    }
    
    List<String> searchContactByNumber(String number, List<Contact> contacts) throws ContactNotFoundException
    {
        List<String> searches = new ArrayList<>();
        
        for(Contact c : contacts)
        {
            List<String> l = c.getContactNumber();
            for(String x : l)
                if(x.contains(number))
                    searches.add(x);
        }
        
        if(searches.isEmpty())
            throw new ContactNotFoundException("No numbers found!!");
        
        return searches;
    }
    
    void addContactNumber(int contactId, String contactNo, List<Contact> contacts)
    {
        for(Contact c : contacts)
        {
            if(c.getContactId() == contactId)
            {
                List<String> n = c.getContactNumber();
                n.add(contactNo);
                c.setContactNumber(n);
                System.out.println("Added Successfully!!");
                return;
            }
        }
    }
    
    void sortContactsByName(List<Contact> contacts)
    {
        Collections.sort(contacts);
        int i;
        Contact c;
        
        for(i=0; i<contacts.size(); i++)
        {
            c = contacts.get(i);
            System.out.println();
            System.out.println("Contact id : " + c.getContactId());
            System.out.println("Contact Name : " + c.getContactName());
            System.out.println();
        }
    }
    
    void readContactsFromFile(List<Contact> contacts, String fname) throws Exception
    {
        FileInputStream fin = new FileInputStream(fname);
        Scanner scan = new Scanner(fin);
        String data[];
        Contact c;
        List<String> l;
        int i;
        
        while(scan.hasNextLine())
        {
            data = scan.nextLine().split(",");
            c = new Contact();
            
            l = new ArrayList<>();
            c.setContactId(Integer.parseInt(data[0].trim()));
            c.setContactName(data[1]);
            c.setEmail(data[2]);
            if(data.length > 3)
            {//contact number exists
                for(i=3; i<data.length; i++)
                {
                    l.add(data[i]);
                    System.out.println(l.get(l.size()-1));
                }
            }
            c.setContactNumber(l);
            contacts.add(c);
        }
        System.out.println("Read Successfully!!");
    }
    
    void display(List<Contact> contacts)
    {
        int i, j;
        Contact c;
        String s;
        List<String> l;
        
        for(i=0; i<contacts.size(); i++)
        {
            s="";
            c = contacts.get(i);
            System.out.println("ID : " +  c.getContactId());
            System.out.println("Name : "+ c.getContactName());
            System.out.println("Email : " + c.getEmail());
            l =  c.getContactNumber();
            
            if(l!=null)
            {
                for(j=0; j<l.size(); j++)
                    if(j+1 == l.size())
                        s+=l.get(j);
                    else
                        s+=l.get(j)+",";
                if(s.length() > 0)
                    System.out.println("Contact Numbers  : "+s);
            }
        }
    }
    
    void serializeContactDetails(List<Contact> contacts , String fname) throws Exception
    {
        FileOutputStream fout = new FileOutputStream(fname);
        ObjectOutputStream out = new ObjectOutputStream(fout);
        
        for(Contact c : contacts)
            out.writeObject(c);
        
        System.out.println("Serialized in "+fname);
    }
    
    List<Contact> deserializeContact(String fname, List<Contact> contacts)  throws Exception
    {
        List<Contact> obj = new ArrayList<>();
        
        FileInputStream fin  =new FileInputStream(fname);
        ObjectInputStream oin = new ObjectInputStream(fin);
        
        for(int i=0; i<contacts.size(); i++)
        {
            obj.add((Contact)oin.readObject());
        }
        
        return obj;
    }
    
    boolean addContacts(List<Contact> existingContact,Set<Contact> newContacts)
    {
        existingContact.addAll(newContacts);
        return true;
    }
    
    Set<Contact> populateContactFromDb() throws Exception
    {
        Connection conn;
        Statement stmt;
        ResultSet rs;
        Set<Contact> s = new HashSet<>();
        Contact c;
        String url, uname, pwd, driver, temp;
        int cols, i;
        List<Object> t = new ArrayList<>();
        
        url = "jdbc:mysql://localhost:3306/contactdb";
        driver = "com.mysql.cj.jdbc.Driver";
        uname = "root";
        pwd = "";
        
        Class.forName(driver);
        conn = DriverManager.getConnection(url, uname, pwd);
        
        stmt = conn.createStatement();
        rs = stmt.executeQuery("select * from contact_tbl");
        ResultSetMetaData rsmd = rs.getMetaData();
        cols = rsmd.getColumnCount();
        
        while(rs.next())
        {
            c = new Contact();
            c.setContactId(Integer.parseInt(rs.getString(1)));
            c.setContactName(rs.getString(2));
            c.setEmail(rs.getString(3));
            temp = rs.getString(4);
            if(temp!=null)
                c.setContactNumber(Arrays.asList(temp.split(",")));
            
            s.add(c);
        }
        
        conn.close();
        stmt.close();
        return s;
    }
    
    public static void main(String args[])
    {
        int ch=0, id, i=0;
        List<Contact> contacts = new ArrayList<>();
        List<String> l;
        List<Contact> temp;
        Scanner scan = new Scanner(System.in);
        ContactService cs = new ContactService();
        Contact c;
        
        try
        {
            while(ch!=12)
            {
                System.out.println("1. Add a contact : ");
                System.out.println("2. Remove a contact");
                System.out.println("3. Search contact by name");
                System.out.println("4. Search by number");
                System.out.println("5. Add Contact number to existing contacts");
                System.out.println("6. Sort Contacts By Name");
                System.out.println("7. Read from file");
                System.out.println("8. Display");
                System.out.println("9. Serialize Objects");
                System.out.println("10.Deserialize Objects");
                System.out.println("11. Populate from DB");
                System.out.println("12. Exit");
                System.out.println("Enter choice  : ");
                ch  = scan.nextInt();
                
                switch(ch)
                {
                    case 1: cs.addContact(new Contact(), contacts);
                            System.out.println("Added Successfully");
                            break;
                            
                    case 2: System.out.println("Enter contact id to remove : ");
                            id  =scan.nextInt();
                            c = new Contact();
                            c.setContactId(id);
                            cs.removeContact(c, contacts);
                            break;
                          
                    case 3: System.out.println("Enter name : ");        
                            scan.nextLine();
                            c = cs.searchContactByName(scan.nextLine(), contacts);
                            System.out.println("CONTACT FOUND!!");
                            System.out.println("Contac Id : " + c.getContactId());
                            System.out.println("Contac Name : " + c.getContactName());
                            System.out.println("Contact Email : "+ c.getEmail());
                            break;
                            
                    case 4: System.out.println("Enter Number : ");
                            scan.nextLine();
                            l = cs.searchContactByNumber(scan.nextLine(), contacts);
                            System.out.println("NUMBERS FOUND: ");
                            for(String x: l)
                                System.out.println(x);
                            break;
                          
                    case 5: System.out.println("Enter contact id : ");
                            id = scan.nextInt();
                            scan.nextLine();
                            System.out.println("Enter contact number : ");
                            cs.addContactNumber(id, scan.nextLine(), contacts);
                            break;
                            
                    case 6: cs.sortContactsByName(contacts);
                            break;
                            
                            
                    case 7: cs.readContactsFromFile(contacts, "e:/Contact.txt");
                            break;
                          
                    case 8: cs.display(contacts);
                            break;
                            
                    case 9: cs.serializeContactDetails(contacts , "file.txt");
                            break;
                            
                    case 10: temp = cs.deserializeContact("file.txt", contacts);
                             System.out.println("Deserialized Objects :");
                             cs.display(temp);
                             break;
                             
                             
                    case 11: if(cs.addContacts(contacts, cs.populateContactFromDb()))  
                                System.out.println("Populated Succesfully!!");
                             break;
                             
                    case 12: break;
                }
            }
        }
        catch(ContactNotFoundException cnfe)
        {
            System.out.println(cnfe);
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }  
}
