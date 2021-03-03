package javadb;


public class ContactNotFoundException extends Exception {
        String msg;
        
        ContactNotFoundException(String s)
        {
            msg  =s;
        }
        
        @Override
        public String toString()
        {
            return "ContactNotFoundException : "+msg;
        }
}
