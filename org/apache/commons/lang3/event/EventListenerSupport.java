package org.apache.commons.lang3.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.Validate;

public class EventListenerSupport implements Serializable {
   private static final long serialVersionUID = 3593265990380473632L;
   private List listeners;
   private transient Object proxy;
   private transient Object[] prototypeArray;

   public static EventListenerSupport create(Class listenerInterface) {
      return new EventListenerSupport(listenerInterface);
   }

   public EventListenerSupport(Class listenerInterface) {
      this(listenerInterface, Thread.currentThread().getContextClassLoader());
   }

   public EventListenerSupport(Class listenerInterface, ClassLoader classLoader) {
      this();
      Validate.notNull(listenerInterface, "Listener interface cannot be null.", new Object[0]);
      Validate.notNull(classLoader, "ClassLoader cannot be null.", new Object[0]);
      Validate.isTrue(listenerInterface.isInterface(), "Class {0} is not an interface", new Object[]{listenerInterface.getName()});
      this.initializeTransientFields(listenerInterface, classLoader);
   }

   private EventListenerSupport() {
      this.listeners = new CopyOnWriteArrayList();
   }

   public Object fire() {
      return this.proxy;
   }

   public void addListener(Object listener) {
      Validate.notNull(listener, "Listener object cannot be null.", new Object[0]);
      this.listeners.add(listener);
   }

   int getListenerCount() {
      return this.listeners.size();
   }

   public void removeListener(Object listener) {
      Validate.notNull(listener, "Listener object cannot be null.", new Object[0]);
      this.listeners.remove(listener);
   }

   public Object[] getListeners() {
      return this.listeners.toArray(this.prototypeArray);
   }

   private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
      ArrayList<L> serializableListeners = new ArrayList();
      ObjectOutputStream testObjectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());

      for(L listener : this.listeners) {
         try {
            testObjectOutputStream.writeObject(listener);
            serializableListeners.add(listener);
         } catch (IOException var7) {
            testObjectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
         }
      }

      objectOutputStream.writeObject(serializableListeners.toArray(this.prototypeArray));
   }

   private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
      L[] srcListeners = (Object[])((Object[])objectInputStream.readObject());
      this.listeners = new CopyOnWriteArrayList(srcListeners);
      Class<L> listenerInterface = srcListeners.getClass().getComponentType();
      this.initializeTransientFields(listenerInterface, Thread.currentThread().getContextClassLoader());
   }

   private void initializeTransientFields(Class listenerInterface, ClassLoader classLoader) {
      L[] array = (Object[])((Object[])Array.newInstance(listenerInterface, 0));
      this.prototypeArray = array;
      this.createProxy(listenerInterface, classLoader);
   }

   private void createProxy(Class listenerInterface, ClassLoader classLoader) {
      this.proxy = listenerInterface.cast(Proxy.newProxyInstance(classLoader, new Class[]{listenerInterface}, this.createInvocationHandler()));
   }

   protected InvocationHandler createInvocationHandler() {
      return new EventListenerSupport.ProxyInvocationHandler();
   }

   protected class ProxyInvocationHandler implements InvocationHandler {
      public Object invoke(Object unusedProxy, Method method, Object[] args) throws Throwable {
         for(L listener : EventListenerSupport.this.listeners) {
            method.invoke(listener, args);
         }

         return null;
      }
   }
}
