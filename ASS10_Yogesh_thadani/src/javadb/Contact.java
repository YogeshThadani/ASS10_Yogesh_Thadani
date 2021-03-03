package javadb;

import java.util.*;
public class Contact implements Comparable<Contact>, java.io.Serializable
{
	private int contactId;
	private String contactName;
	private String email;
	private List<String> contactNumber;
        
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(List<String> contactNumber) {
		this.contactNumber = contactNumber;
	}
        
        public int compareTo(Contact c)
        {
            return this.getContactName().compareTo(c.getContactName());
        }
}
