package tv.twitch;

import tv.twitch.CoreAPI;
import tv.twitch.ErrorCode;
import tv.twitch.MessageLevel;

public class Core {
   private static Core s_Instance = null;
   private CoreAPI m_CoreAPI = null;
   private String m_ClientId = null;
   private int m_NumInitializations = 0;

   public static Core getInstance() {
      return s_Instance;
   }

   public Core(CoreAPI var1) {
      this.m_CoreAPI = var1;
      if(s_Instance == null) {
         s_Instance = this;
      }

   }

   public boolean getIsInitialized() {
      return this.m_NumInitializations > 0;
   }

   public ErrorCode initialize(String var1, String var2) {
      if(this.m_NumInitializations == 0) {
         this.m_ClientId = var1;
      } else if(var1 != this.m_ClientId) {
         return ErrorCode.TTV_EC_INVALID_CLIENTID;
      }

      ++this.m_NumInitializations;
      if(this.m_NumInitializations > 1) {
         return ErrorCode.TTV_EC_SUCCESS;
      } else {
         ErrorCode var3 = this.m_CoreAPI.init(var1, var2);
         if(ErrorCode.failed(var3)) {
            --this.m_NumInitializations;
            this.m_ClientId = null;
         }

         return var3;
      }
   }

   public ErrorCode shutdown() {
      if(this.m_NumInitializations == 0) {
         return ErrorCode.TTV_EC_NOT_INITIALIZED;
      } else {
         --this.m_NumInitializations;
         ErrorCode var1 = ErrorCode.TTV_EC_SUCCESS;
         if(this.m_NumInitializations == 0) {
            var1 = this.m_CoreAPI.shutdown();
            if(ErrorCode.failed(var1)) {
               ++this.m_NumInitializations;
            } else if(s_Instance == this) {
               s_Instance = null;
            }
         }

         return var1;
      }
   }

   public ErrorCode setTraceLevel(MessageLevel var1) {
      ErrorCode var2 = this.m_CoreAPI.setTraceLevel(var1);
      return var2;
   }

   public String errorToString(ErrorCode var1) {
      String var2 = this.m_CoreAPI.errorToString(var1);
      return var2;
   }
}
