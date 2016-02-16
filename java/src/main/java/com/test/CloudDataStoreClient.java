package com.test;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionRequest;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionResponse;
import com.google.api.services.datastore.DatastoreV1.CommitRequest;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.LookupRequest;
import com.google.api.services.datastore.DatastoreV1.LookupResponse;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreFactory;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.api.services.datastore.client.DatastoreOptions;
import com.google.protobuf.ByteString;



public class CloudDataStoreClient {



	
	public CloudDataStoreClient() {
		try {
			
	        ConsoleHandler consoleHandler = new ConsoleHandler();
	        consoleHandler.setLevel(Level.ALL);
	        consoleHandler.setFormatter(new SimpleFormatter());
	        
	        Logger logger = Logger.getLogger("com.google.api.client");
	        logger.setLevel(Level.ALL);
	        logger.addHandler(consoleHandler);  
	        
			Logger lh = Logger.getLogger("httpclient.wire.header");
			lh.setLevel(Level.ALL);
			lh.addHandler(consoleHandler);
			
			Logger lc = Logger.getLogger("httpclient.wire.content");
			lc.setLevel(Level.ALL);
			lc.addHandler(consoleHandler);				

			
			  HttpTransport httpTransport = new NetHttpTransport();
			  JacksonFactory jsonFactory = new JacksonFactory();

			GoogleCredential credential = GoogleCredential.getApplicationDefault();
			  	credential.setAccessToken("foo");
			    String datasetID = "p0";
			    DatastoreOptions b = DatastoreHelper.getOptionsfromEnv().dataset(datasetID).credential(credential).build();
			    	
			    Datastore datastore =  DatastoreFactory.get().create(b);			    

			      LookupRequest.Builder lreq = LookupRequest.newBuilder();
			      Key.Builder key = Key.newBuilder().addPathElement(
			          Key.PathElement.newBuilder().setKind("Employee").setName("aguadypoogznoqofmgmy"));
			      lreq.addKey(key);
			      LookupResponse lresp = datastore.lookup(lreq.build());
			      if (lresp.getFoundCount() > 0) {
			        Entity e = lresp.getFound(0).getEntity();
			        System.out.println(e);
			      }
			      else {
			    	  
			          BeginTransactionRequest.Builder treq = BeginTransactionRequest.newBuilder();
			          BeginTransactionResponse tres = datastore.beginTransaction(treq.build());
			          ByteString tx = tres.getTransaction();			    	  
			          CommitRequest.Builder creq = CommitRequest.newBuilder();			    	  
			    	  creq.setTransaction(tx);
			    	  
			    	  Entity entity;
			          Entity.Builder entityBuilder = Entity.newBuilder();
			          entityBuilder.setKey(key);
			          entityBuilder.addProperty(Property.newBuilder()
			              .setName("some_string")
			              .setValue(Value.newBuilder().setStringValue("some value")));
			          entity = entityBuilder.build();
			          creq.getMutationBuilder().addInsert(entity);		
			          datastore.commit(creq.build());
			      }

		}
		catch (Exception ex) {
			System.out.println("Error : " + ex);
		}
	}

}
