package org.apache.logging.log4j.core.appender.db.jpa;

import java.lang.reflect.Constructor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.appender.db.jpa.AbstractLogEventWrapperEntity;

public final class JPADatabaseManager extends AbstractDatabaseManager {
   private static final JPADatabaseManager.JPADatabaseManagerFactory FACTORY = new JPADatabaseManager.JPADatabaseManagerFactory();
   private final String entityClassName;
   private final Constructor entityConstructor;
   private final String persistenceUnitName;
   private EntityManagerFactory entityManagerFactory;

   private JPADatabaseManager(String name, int bufferSize, Class entityClass, Constructor entityConstructor, String persistenceUnitName) {
      super(name, bufferSize);
      this.entityClassName = entityClass.getName();
      this.entityConstructor = entityConstructor;
      this.persistenceUnitName = persistenceUnitName;
   }

   protected void connectInternal() {
      this.entityManagerFactory = Persistence.createEntityManagerFactory(this.persistenceUnitName);
   }

   protected void disconnectInternal() {
      if(this.entityManagerFactory != null && this.entityManagerFactory.isOpen()) {
         this.entityManagerFactory.close();
      }

   }

   protected void writeInternal(LogEvent event) {
      if(this.isConnected() && this.entityManagerFactory != null) {
         AbstractLogEventWrapperEntity entity;
         try {
            entity = (AbstractLogEventWrapperEntity)this.entityConstructor.newInstance(new Object[]{event});
         } catch (Exception var10) {
            throw new AppenderLoggingException("Failed to instantiate entity class [" + this.entityClassName + "].", var10);
         }

         EntityManager entityManager = null;
         EntityTransaction transaction = null;

         try {
            entityManager = this.entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(entity);
            transaction.commit();
         } catch (Exception var11) {
            if(transaction != null && transaction.isActive()) {
               transaction.rollback();
            }

            throw new AppenderLoggingException("Failed to insert record for log event in JDBC manager: " + var11.getMessage(), var11);
         } finally {
            if(entityManager != null && entityManager.isOpen()) {
               entityManager.close();
            }

         }

      } else {
         throw new AppenderLoggingException("Cannot write logging event; JPA manager not connected to the database.");
      }
   }

   public static JPADatabaseManager getJPADatabaseManager(String name, int bufferSize, Class entityClass, Constructor entityConstructor, String persistenceUnitName) {
      return (JPADatabaseManager)AbstractDatabaseManager.getManager(name, new JPADatabaseManager.FactoryData(bufferSize, entityClass, entityConstructor, persistenceUnitName), FACTORY);
   }

   private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData {
      private final Class entityClass;
      private final Constructor entityConstructor;
      private final String persistenceUnitName;

      protected FactoryData(int bufferSize, Class entityClass, Constructor entityConstructor, String persistenceUnitName) {
         super(bufferSize);
         this.entityClass = entityClass;
         this.entityConstructor = entityConstructor;
         this.persistenceUnitName = persistenceUnitName;
      }
   }

   private static final class JPADatabaseManagerFactory implements ManagerFactory {
      private JPADatabaseManagerFactory() {
      }

      public JPADatabaseManager createManager(String name, JPADatabaseManager.FactoryData data) {
         return new JPADatabaseManager(name, data.getBufferSize(), data.entityClass, data.entityConstructor, data.persistenceUnitName);
      }
   }
}
