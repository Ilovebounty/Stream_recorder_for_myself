package main;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;


public class Email {
	public Email() {}

	  //
	  // inspired by :
	  // http://www.mikedesjardins.net/content/2008/03/using-javamail-to-read-and-extract/
	  //

	  public static String doit() throws MessagingException, IOException {
	    Folder folder = null;
	    Store store = null;
	    String url1="aucun";
	    
	    try {
	      
	      Properties props = System.getProperties();
	      props.setProperty("mail.store.protocol", "imaps");

	      Session session = Session.getDefaultInstance(props, null);
	      // session.setDebug(true);
	      store = session.getStore("imaps");
	      store.connect("imap-mail.outlook.com","misterpag76@hotmail.fr", "pag.1494");
	      folder = store.getFolder("Inbox");
	      /* Others GMail folders :
	       * [Gmail]/All Mail   This folder contains all of your Gmail messages.
	       * [Gmail]/Drafts     Your drafts.
	       * [Gmail]/Sent Mail  Messages you sent to other people.
	       * [Gmail]/Spam       Messages marked as spam.
	       * [Gmail]/Starred    Starred messages.
	       * [Gmail]/Trash      Messages deleted from Gmail.
	       */
	      folder.open(Folder.READ_WRITE);
	      Message messages[] = folder.getMessages();
	      for (int i=0; i < messages.length; ++i) {
	        Message msg = messages[i];
	        /*
	          if we don''t want to fetch messages already processed
	          if (!msg.isSet(Flags.Flag.SEEN)) {
	             String from = "unknown";
	             ...
	          }
	        */
	        String from = "unknown";
	        Boolean foot;
	        
	        if (msg.getReplyTo().length >= 1) {
	          from = msg.getReplyTo()[0].toString();
	        }
	        else if (msg.getFrom().length >= 1) {
	          from = msg.getFrom()[0].toString();
	        }
	        if(msg.isSet(Flags.Flag.SEEN))
	        	;
	        else
	        {
	        	System.out.println("Nouveau message trouvé");
	        	String subject = msg.getSubject();
	        	String adresse = msg.getFrom()[0].toString();
	        	//if(!adresse.contains("camsoda"))
	        	//	msg.getContent();
	        	foot = subject.contains("Foot");
		        
		        
		        if(foot)
		        	url1 = "url of your football match";
		        
	        }
	        
	        	
	        String contenu = null;
	        if(msg.isMimeType("text/plain"))
	        {
	        	contenu = msg.getContent().toString();
	        }
	        else if (msg.isMimeType("multipart/*")) {
	          MimeMultipart mimeMultipart = (MimeMultipart) msg.getContent();
	          contenu = getTextFromMimeMultipart(mimeMultipart);
	        }
	        else
	        {
	        	String html = (String) msg.getContent();
	        	contenu = contenu + "\n" + org.jsoup.Jsoup.parse(html).text();
	        }

	        // you may want to replace the spaces with "_"
	        // the TEMP directory is used to store the files
	       // String filename = "C:/temp/" +  subject;
	        //saveParts(msg.getContent(), filename);
	        //msg.setFlag(Flags.Flag.SEEN,true);
	        // to delete the message
	        // msg.setFlag(Flags.Flag.DELETED, true);
	      }
	    }
	    finally {
	      if (folder != null) { folder.close(true); }
	      if (store != null) { store.close(); }
	    }
		return url1;
	  }
	  
	  private static String getTextFromMimeMultipart(
		        MimeMultipart mimeMultipart)  throws MessagingException, IOException{
		    String result = "";
		    int count = mimeMultipart.getCount();
		    for (int i = 0; i < count; i++) {
		        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
		        if (bodyPart.isMimeType("text/plain")) {
		            result = result + "\n" + bodyPart.getContent();
		            break; // without break same text appears twice in my tests
		        } else if (bodyPart.isMimeType("text/html")) {
		            String html = (String) bodyPart.getContent();
		            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
		        } else if (bodyPart.getContent() instanceof MimeMultipart){
		            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
		        }
		    }
		    return result;
		}

	  public static void saveParts(Object content, String filename)
	  throws IOException, MessagingException
	  {
	    OutputStream out = null;
	    InputStream in = null;
	    try {
	      if (content instanceof Multipart) {
	        Multipart multi = ((Multipart)content);
	        int parts = multi.getCount();
	        for (int j=0; j < parts; ++j) {
	          MimeBodyPart part = (MimeBodyPart)multi.getBodyPart(j);
	          if (part.getContent() instanceof Multipart) {
	            // part-within-a-part, do some recursion...
	            saveParts(part.getContent(), filename);
	          }
	          else {
	            String extension = "";
	            if (part.isMimeType("text/html")) {
	              extension = "html";
	            }
	            else {
	              if (part.isMimeType("text/plain")) {
	                extension = "txt";
	              }
	              else {
	                //  Try to get the name of the attachment
	                extension = part.getDataHandler().getName();
	              }
	              filename = filename + "." + extension;
	              System.out.println("... " + filename);
	              out = new FileOutputStream(new File(filename));
	              in = part.getInputStream();
	              int k;
	              while ((k = in.read()) != -1) {
	                out.write(k);
	              }
	            }
	          }
	        }
	      }
	    }
	    finally {
	      if (in != null) { in.close(); }
	      if (out != null) { out.flush(); out.close(); }
	    }
	  }

	  public static void main(String args[]) throws Exception {
		  
		  while(true)
		  {
			  String myurl= "aucun";
			  while (myurl == "aucun")
			  {
				  System.out.println("recherche de nouveau mail");
				  myurl = Email.doit();
				  Thread.sleep(300000);
			  }
			  ConnexionWeb.Setup(myurl);
		  }
		  	  
	  }
}
