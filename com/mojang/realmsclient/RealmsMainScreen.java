package com.mojang.realmsclient;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.realmsclient.RealmsVersion;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsBuyRealmsScreen;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraft.realms.RealmsServerStatusPinger;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RealmsMainScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static boolean overrideConfigure = false;
   private static boolean stageEnabled = false;
   private boolean dontSetConnectedToRealms = false;
   protected static final int BUTTON_BACK_ID = 0;
   protected static final int BUTTON_PLAY_ID = 1;
   protected static final int BUTTON_CONFIGURE_ID = 2;
   protected static final int BUTTON_LEAVE_ID = 3;
   protected static final int BUTTON_BUY_ID = 4;
   protected static final int RESOURCEPACK_ID = 100;
   private RealmsServer resourcePackServer;
   private static final String ON_ICON_LOCATION = "realms:textures/gui/realms/on_icon.png";
   private static final String OFF_ICON_LOCATION = "realms:textures/gui/realms/off_icon.png";
   private static final String EXPIRED_ICON_LOCATION = "realms:textures/gui/realms/expired_icon.png";
   private static final String INVITATION_ICONS_LOCATION = "realms:textures/gui/realms/invitation_icons.png";
   private static final String INVITE_ICON_LOCATION = "realms:textures/gui/realms/invite_icon.png";
   private static final String WORLDICON_LOCATION = "realms:textures/gui/realms/world_icon.png";
   private static final String LOGO_LOCATION = "realms:textures/gui/title/realms.png";
   private static RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
   private static RealmsServerStatusPinger statusPinger = new RealmsServerStatusPinger();
   private static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
   private static int lastScrollYPosition = -1;
   private RealmsScreen lastScreen;
   private volatile RealmsMainScreen.ServerSelectionList serverSelectionList;
   private long selectedServerId = -1L;
   private RealmsButton configureButton;
   private RealmsButton leaveButton;
   private RealmsButton playButton;
   private RealmsButton buyButton;
   private String toolTip;
   private List realmsServers = Lists.newArrayList();
   private static final String mcoInfoUrl = "https://minecraft.net/realms";
   private volatile int numberOfPendingInvites = 0;
   private int animTick;
   private static volatile boolean mcoEnabled;
   private static volatile boolean mcoEnabledCheck;
   private static boolean checkedMcoAvailability;
   private static volatile boolean trialsAvailable;
   private static volatile boolean createdTrial = false;
   private static final ReentrantLock trialLock = new ReentrantLock();
   private static RealmsScreen realmsGenericErrorScreen = null;
   private static boolean regionsPinged = false;
   private boolean onLink = false;
   private int mindex = 0;
   private char[] mchars = new char[]{'3', '2', '1', '4', '5', '6'};
   private int sindex = 0;
   private char[] schars = new char[]{'9', '8', '7', '1', '2', '3'};

   public RealmsMainScreen(RealmsScreen lastScreen) {
      this.lastScreen = lastScreen;
      this.checkIfMcoEnabled();
   }

   public void mouseEvent() {
      super.mouseEvent();
      this.serverSelectionList.mouseEvent();
   }

   public void init() {
      if(!this.dontSetConnectedToRealms) {
         Realms.setConnectedToRealms(false);
      }

      if(realmsGenericErrorScreen != null) {
         Realms.setScreen(realmsGenericErrorScreen);
      } else {
         Keyboard.enableRepeatEvents(true);
         this.buttonsClear();
         this.postInit();
         if(this.isMcoEnabled()) {
            realmsDataFetcher.init();
         }

      }
   }

   public void postInit() {
      this.buttonsAdd(this.playButton = newButton(1, this.width() / 2 - 154, this.height() - 52, 154, 20, getLocalizedString("mco.selectServer.play")));
      this.buttonsAdd(this.configureButton = newButton(2, this.width() / 2 + 6, this.height() - 52, 154, 20, getLocalizedString("mco.selectServer.configure")));
      this.buttonsAdd(this.leaveButton = newButton(3, this.width() / 2 - 154, this.height() - 28, 102, 20, getLocalizedString("mco.selectServer.leave")));
      this.buttonsAdd(this.buyButton = newButton(4, this.width() / 2 - 48, this.height() - 28, 102, 20, getLocalizedString("mco.selectServer.buy")));
      this.buttonsAdd(newButton(0, this.width() / 2 + 58, this.height() - 28, 102, 20, getLocalizedString("gui.back")));
      this.serverSelectionList = new RealmsMainScreen.ServerSelectionList();
      if(lastScrollYPosition != -1) {
         this.serverSelectionList.scroll(lastScrollYPosition);
      }

      RealmsServer server = this.findServer(this.selectedServerId);
      this.playButton.active(server != null && server.state == RealmsServer.State.OPEN && !server.expired);
      this.configureButton.active(overrideConfigure || server != null && server.state != RealmsServer.State.ADMIN_LOCK && server.ownerUUID.equals(Realms.getUUID()));
      this.leaveButton.active(server != null && !server.ownerUUID.equals(Realms.getUUID()));
   }

   public void tick() {
      ++this.animTick;
      if(this.noParentalConsent()) {
         Realms.setScreen(new RealmsParentalConsentScreen(this.lastScreen));
      }

      if(this.isMcoEnabled()) {
         realmsDataFetcher.init();
         if(realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            List<RealmsServer> newServers = realmsDataFetcher.getServers();
            boolean ownsNonExpiredRealmServer = false;

            label85:
            for(RealmsServer retrievedServer : newServers) {
               if(this.isSelfOwnedNonExpiredServer(retrievedServer)) {
                  ownsNonExpiredRealmServer = true;
               }

               Iterator i$ = this.realmsServers.iterator();

               RealmsServer oldServer;
               while(true) {
                  if(!i$.hasNext()) {
                     continue label85;
                  }

                  oldServer = (RealmsServer)i$.next();
                  if(retrievedServer.id == oldServer.id) {
                     break;
                  }
               }

               retrievedServer.latestStatFrom(oldServer);
            }

            this.realmsServers = newServers;
            if(!regionsPinged && ownsNonExpiredRealmServer) {
               regionsPinged = true;
               this.pingRegions();
            }
         }

         if(realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
         }

         if(realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !createdTrial) {
            trialsAvailable = realmsDataFetcher.isTrialAvailable();
         }

         realmsDataFetcher.markClean();
      }
   }

   private void pingRegions() {
      (new Thread() {
         public void run() {
            List<RegionPingResult> regionPingResultList = Ping.pingAllRegions();
            RealmsClient client = RealmsClient.createRealmsClient();
            PingResult pingResult = new PingResult();
            pingResult.pingResults = regionPingResultList;
            pingResult.worldIds = RealmsMainScreen.this.getOwnedNonExpiredWorldIds();

            try {
               client.sendPingResults(pingResult);
            } catch (Throwable var5) {
               RealmsMainScreen.LOGGER.warn("Could not send ping result to Realms: ", var5);
            }

         }
      }).start();
   }

   private List getOwnedNonExpiredWorldIds() {
      List<Long> ids = new ArrayList();

      for(RealmsServer server : this.realmsServers) {
         if(this.isSelfOwnedNonExpiredServer(server)) {
            ids.add(Long.valueOf(server.id));
         }
      }

      return ids;
   }

   private boolean isMcoEnabled() {
      return mcoEnabled;
   }

   private boolean noParentalConsent() {
      return mcoEnabledCheck && !mcoEnabled;
   }

   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void buttonClicked(RealmsButton button) {
      if(button.active()) {
         switch(button.id()) {
         case 0:
            this.stopRealmsFetcherAndPinger();
            Realms.setScreen(this.lastScreen);
            break;
         case 1:
            this.play(this.findServer(this.selectedServerId));
            break;
         case 2:
            this.configureClicked();
            break;
         case 3:
            this.leaveClicked();
            break;
         case 4:
            this.saveListScrollPosition();
            this.stopRealmsFetcherAndPinger();
            Realms.setScreen(new RealmsBuyRealmsScreen(this));
            break;
         default:
            return;
         }

      }
   }

   private void createTrial() {
      if(createdTrial) {
         trialsAvailable = false;
      } else {
         (new Thread("Realms-create-trial") {
            public void run() {
               try {
                  if(RealmsMainScreen.trialLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
                     RealmsClient client = RealmsClient.createRealmsClient();
                     RealmsMainScreen.trialsAvailable = false;
                     if(client.createTrial().booleanValue()) {
                        RealmsMainScreen.createdTrial = true;
                        RealmsMainScreen.realmsDataFetcher.forceUpdate();
                     } else {
                        Realms.setScreen(new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.trial.unavailable"), RealmsMainScreen.this));
                     }

                     return;
                  }
               } catch (RealmsServiceException var7) {
                  RealmsMainScreen.LOGGER.error("Trials wasn\'t available: " + var7.toString());
                  Realms.setScreen(new RealmsGenericErrorScreen(var7, RealmsMainScreen.this));
                  return;
               } catch (IOException var8) {
                  RealmsMainScreen.LOGGER.error("Couldn\'t parse response when trying to create trial: " + var8.toString());
                  RealmsMainScreen.trialsAvailable = false;
                  return;
               } catch (InterruptedException var9) {
                  RealmsMainScreen.LOGGER.error("Trial Interrupted exception: " + var9.toString());
                  return;
               } finally {
                  if(RealmsMainScreen.trialLock.isHeldByCurrentThread()) {
                     RealmsMainScreen.trialLock.unlock();
                  }

               }

            }
         }).start();
      }
   }

   private void checkIfMcoEnabled() {
      if(!checkedMcoAvailability) {
         checkedMcoAvailability = true;
         (new Thread("MCO Availability Checker #1") {
            public void run() {
               RealmsClient client = RealmsClient.createRealmsClient();

               try {
                  RealmsClient.CompatibleVersionResponse versionResponse = client.clientCompatible();
                  if(versionResponse.equals(RealmsClient.CompatibleVersionResponse.OUTDATED)) {
                     Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true));
                     return;
                  }

                  if(versionResponse.equals(RealmsClient.CompatibleVersionResponse.OTHER)) {
                     Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false));
                     return;
                  }
               } catch (RealmsServiceException var9) {
                  RealmsMainScreen.checkedMcoAvailability = false;
                  RealmsMainScreen.LOGGER.error("Couldn\'t connect to realms: ", new Object[]{var9.toString()});
                  if(var9.httpResultCode == 401) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(var9, RealmsMainScreen.this.lastScreen);
                  }

                  Realms.setScreen(new RealmsGenericErrorScreen(var9, RealmsMainScreen.this.lastScreen));
                  return;
               } catch (IOException var10) {
                  RealmsMainScreen.checkedMcoAvailability = false;
                  RealmsMainScreen.LOGGER.error("Couldn\'t connect to realms: ", new Object[]{var10.getMessage()});
                  Realms.setScreen(new RealmsGenericErrorScreen(var10.getMessage(), RealmsMainScreen.this.lastScreen));
                  return;
               }

               boolean retry = false;

               for(int i = 0; i < 3; ++i) {
                  try {
                     Boolean result = client.mcoEnabled();
                     if(result.booleanValue()) {
                        RealmsMainScreen.LOGGER.info("Realms is available for this user");
                        RealmsMainScreen.mcoEnabled = true;
                     } else {
                        RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                        RealmsMainScreen.mcoEnabled = false;
                     }

                     RealmsMainScreen.mcoEnabledCheck = true;
                  } catch (RetryCallException var6) {
                     retry = true;
                  } catch (RealmsServiceException var7) {
                     RealmsMainScreen.LOGGER.error("Couldn\'t connect to Realms: " + var7.toString());
                  } catch (IOException var8) {
                     RealmsMainScreen.LOGGER.error("Couldn\'t parse response connecting to Realms: " + var8.getMessage());
                  }

                  if(!retry) {
                     break;
                  }

                  try {
                     Thread.sleep(5000L);
                  } catch (InterruptedException var5) {
                     Thread.currentThread().interrupt();
                  }
               }

            }
         }).start();
      }

   }

   private void switchToStage() {
      if(!stageEnabled) {
         (new Thread("MCO Stage Availability Checker #1") {
            public void run() {
               RealmsClient client = RealmsClient.createRealmsClient();

               try {
                  Boolean result = client.stageAvailable();
                  if(result.booleanValue()) {
                     RealmsMainScreen.this.stopRealmsFetcherAndPinger();
                     RealmsClient.switchToStage();
                     RealmsMainScreen.LOGGER.info("Switched to stage");
                     RealmsMainScreen.realmsDataFetcher.init();
                     RealmsMainScreen.stageEnabled = true;
                  } else {
                     RealmsMainScreen.stageEnabled = false;
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.LOGGER.error("Couldn\'t connect to Realms: " + var3.toString());
               } catch (IOException var4) {
                  RealmsMainScreen.LOGGER.error("Couldn\'t parse response connecting to Realms: " + var4.getMessage());
               }

            }
         }).start();
      }

   }

   private void switchToProd() {
      if(stageEnabled) {
         stageEnabled = false;
         this.stopRealmsFetcherAndPinger();
         RealmsClient.switchToProd();
         realmsDataFetcher.init();
      }

   }

   private void stopRealmsFetcherAndPinger() {
      if(this.isMcoEnabled()) {
         realmsDataFetcher.stop();
         statusPinger.removeAll();
      }

   }

   private void configureClicked() {
      RealmsServer selectedServer = this.findServer(this.selectedServerId);
      if(selectedServer != null && (Realms.getUUID().equals(selectedServer.ownerUUID) || overrideConfigure)) {
         this.stopRealmsFetcherAndPinger();
         this.saveListScrollPosition();
         Realms.setScreen(new RealmsConfigureWorldScreen(this, selectedServer.id));
      }

   }

   private void leaveClicked() {
      RealmsServer selectedServer = this.findServer(this.selectedServerId);
      if(selectedServer != null && !Realms.getUUID().equals(selectedServer.ownerUUID)) {
         this.saveListScrollPosition();
         String line2 = getLocalizedString("mco.configure.world.leave.question.line1");
         String line3 = getLocalizedString("mco.configure.world.leave.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 3));
      }

   }

   private void saveListScrollPosition() {
      lastScrollYPosition = this.serverSelectionList.getScroll();
   }

   private RealmsServer findServer(long id) {
      for(RealmsServer server : this.realmsServers) {
         if(server.id == id) {
            return server;
         }
      }

      return null;
   }

   private int findIndex(long serverId) {
      for(int i = 0; i < this.realmsServers.size(); ++i) {
         if(((RealmsServer)this.realmsServers.get(i)).id == serverId) {
            return i;
         }
      }

      return -1;
   }

   public void confirmResult(boolean result, int id) {
      if(id == 3) {
         if(result) {
            (new Thread("Realms-leave-server") {
               public void run() {
                  try {
                     RealmsServer server = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                     if(server != null) {
                        RealmsClient client = RealmsClient.createRealmsClient();
                        RealmsMainScreen.realmsDataFetcher.removeItem(server);
                        RealmsMainScreen.this.realmsServers.remove(server);
                        client.uninviteMyselfFrom(server.id);
                        RealmsMainScreen.realmsDataFetcher.removeItem(server);
                        RealmsMainScreen.this.realmsServers.remove(server);
                        RealmsMainScreen.this.updateSelectedItemPointer();
                     }
                  } catch (RealmsServiceException var3) {
                     RealmsMainScreen.LOGGER.error("Couldn\'t configure world");
                     Realms.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this));
                  }

               }
            }).start();
         }

         Realms.setScreen(this);
      } else if(id == 100) {
         if(!result) {
            Realms.setScreen(this);
         } else {
            this.connectToServer(this.resourcePackServer);
         }
      }

   }

   private void updateSelectedItemPointer() {
      int originalIndex = this.findIndex(this.selectedServerId);
      if(this.realmsServers.size() - 1 == originalIndex) {
         --originalIndex;
      }

      if(this.realmsServers.size() == 0) {
         originalIndex = -1;
      }

      if(originalIndex >= 0 && originalIndex < this.realmsServers.size()) {
         this.selectedServerId = ((RealmsServer)this.realmsServers.get(originalIndex)).id;
      }

   }

   public void removeSelection() {
      this.selectedServerId = -1L;
   }

   public void keyPressed(char ch, int eventKey) {
      switch(eventKey) {
      case 1:
         this.mindex = 0;
         this.sindex = 0;
         this.stopRealmsFetcherAndPinger();
         Realms.setScreen(this.lastScreen);
         break;
      case 28:
      case 156:
         this.mindex = 0;
         this.sindex = 0;
         this.buttonClicked(this.playButton);
         break;
      default:
         if(this.mchars[this.mindex] == ch) {
            ++this.mindex;
            if(this.mindex == this.mchars.length) {
               this.mindex = 0;
               overrideConfigure = true;
            }
         } else {
            this.mindex = 0;
         }

         if(this.schars[this.sindex] == ch) {
            ++this.sindex;
            if(this.sindex == this.schars.length) {
               this.sindex = 0;
               if(!stageEnabled) {
                  this.switchToStage();
               } else {
                  this.switchToProd();
               }
            }

            return;
         }

         this.sindex = 0;
      }

   }

   public void render(int xm, int ym, float a) {
      this.toolTip = null;
      this.renderBackground();
      this.serverSelectionList.render(xm, ym, a);
      this.drawRealmsLogo(this.width() / 2 - 50, 7);
      this.renderLink(xm, ym);
      if(this.toolTip != null) {
         this.renderMousehoverTooltip(this.toolTip, xm, ym);
      }

      this.drawInvitationPendingIcon(xm, ym);
      if(stageEnabled) {
         this.renderStage();
      }

      super.render(xm, ym, a);
   }

   private void drawRealmsLogo(int x, int y) {
      RealmsScreen.bind("realms:textures/gui/title/realms.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2 - 5, 0.0F, 0.0F, 200, 50, 200.0F, 50.0F);
      GL11.glPopMatrix();
   }

   public void mouseClicked(int x, int y, int buttonNum) {
      if(this.inPendingInvitationArea(x, y)) {
         this.stopRealmsFetcherAndPinger();
         RealmsPendingInvitesScreen pendingInvitationScreen = new RealmsPendingInvitesScreen(this.lastScreen);
         Realms.setScreen(pendingInvitationScreen);
      }

      if(this.onLink) {
         RealmsUtil.browseTo("https://minecraft.net/realms");
      }

   }

   private void drawInvitationPendingIcon(int xm, int ym) {
      int pendingInvitesCount = this.numberOfPendingInvites;
      boolean hovering = this.inPendingInvitationArea(xm, ym);
      int baseX = this.width() / 2 + 50;
      int baseY = 8;
      if(pendingInvitesCount != 0) {
         float scale = 0.25F + (1.0F + RealmsMth.sin((float)this.animTick * 0.5F)) * 0.25F;
         int color = -16777216 | (int)(scale * 64.0F) << 16 | (int)(scale * 64.0F) << 8 | (int)(scale * 64.0F) << 0;
         this.fillGradient(baseX - 2, 6, baseX + 18, 26, color, color);
         color = -16777216 | (int)(scale * 255.0F) << 16 | (int)(scale * 255.0F) << 8 | (int)(scale * 255.0F) << 0;
         this.fillGradient(baseX - 2, 6, baseX + 18, 7, color, color);
         this.fillGradient(baseX - 2, 6, baseX - 1, 26, color, color);
         this.fillGradient(baseX + 17, 6, baseX + 18, 26, color, color);
         this.fillGradient(baseX - 2, 25, baseX + 18, 26, color, color);
      }

      RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      RealmsScreen.blit(baseX, 2, hovering?16.0F:0.0F, 0.0F, 15, 25, 31.0F, 25.0F);
      GL11.glPopMatrix();
      if(pendingInvitesCount != 0) {
         int spritePos = (Math.min(pendingInvitesCount, 6) - 1) * 8;
         int yOff = (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.animTick) * 0.57F), RealmsMth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPushMatrix();
         RealmsScreen.blit(baseX + 4, 12 + yOff, (float)spritePos, hovering?8.0F:0.0F, 8, 8, 48.0F, 16.0F);
         GL11.glPopMatrix();
      }

      if(hovering) {
         int rx = xm + 12;
         int ry = ym - 12;
         String message = pendingInvitesCount == 0?getLocalizedString("mco.invites.nopending"):getLocalizedString("mco.invites.pending");
         int width = this.fontWidth(message);
         this.fillGradient(rx - 3, ry - 3, rx + width + 3, ry + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(message, rx, ry, -1);
      }

   }

   private boolean inPendingInvitationArea(int xm, int ym) {
      int x1 = this.width() / 2 + 50;
      int x2 = this.width() / 2 + 66;
      int y1 = 13;
      int y2 = 27;
      return x1 <= xm && xm <= x2 && y1 <= ym && ym <= y2;
   }

   public void play(RealmsServer server) {
      if(server != null) {
         this.stopRealmsFetcherAndPinger();
         this.dontSetConnectedToRealms = true;
         if(server.resourcePackUrl != null && server.resourcePackHash != null) {
            this.resourcePackServer = server;
            this.saveListScrollPosition();
            String line2 = getLocalizedString("mco.configure.world.resourcepack.question.line1");
            String line3 = getLocalizedString("mco.configure.world.resourcepack.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 100));
         } else {
            this.connectToServer(server);
         }
      }

   }

   private void connectToServer(RealmsServer server) {
      RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this, new RealmsTasks.RealmsConnectTask(this, server));
      longRunningMcoTaskScreen.start();
      Realms.setScreen(longRunningMcoTaskScreen);
   }

   private boolean isSelfOwnedServer(RealmsServer serverData) {
      return serverData.ownerUUID != null && serverData.ownerUUID.equals(Realms.getUUID());
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer serverData) {
      return serverData.ownerUUID != null && serverData.ownerUUID.equals(Realms.getUUID()) && !serverData.expired;
   }

   private void drawExpired(int x, int y, int xm, int ym) {
      RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
      if(xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < this.height() - 64 && ym > 32) {
         this.toolTip = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void drawExpiring(int x, int y, int xm, int ym, int daysLeft) {
      if(this.animTick % 20 < 10) {
         RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
         GL11.glPopMatrix();
      }

      if(xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < this.height() - 64 && ym > 32) {
         if(daysLeft == 0) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.soon");
         } else if(daysLeft == 1) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[]{Integer.valueOf(daysLeft)});
         }
      }

   }

   private void drawOpen(int x, int y, int xm, int ym) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
      if(xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < this.height() - 64 && ym > 32) {
         this.toolTip = getLocalizedString("mco.selectServer.open");
      }

   }

   private void drawClose(int x, int y, int xm, int ym) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
      if(xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < this.height() - 64 && ym > 32) {
         this.toolTip = getLocalizedString("mco.selectServer.closed");
      }

   }

   private void drawLocked(int x, int y, int xm, int ym) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
      if(xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < this.height() - 64 && ym > 32) {
         this.toolTip = getLocalizedString("mco.selectServer.locked");
      }

   }

   protected void renderMousehoverTooltip(String msg, int x, int y) {
      if(msg != null) {
         int rx = x + 12;
         int ry = y - 12;
         int index = 0;
         int width = 0;

         for(String s : msg.split("\n")) {
            int the_width = this.fontWidth(s);
            if(the_width > width) {
               width = the_width;
            }
         }

         for(String s : msg.split("\n")) {
            this.fillGradient(rx - 3, ry - (index == 0?3:0) + index, rx + width + 3, ry + 8 + 3 + index, -1073741824, -1073741824);
            this.fontDrawShadow(s, rx, ry + index, 16777215);
            index += 10;
         }

      }
   }

   private void renderLink(int xm, int ym) {
      String text = getLocalizedString("mco.selectServer.whatisrealms");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      int textWidth = this.fontWidth(text);
      int leftPadding = 10;
      int topPadding = 12;
      int x2 = leftPadding + textWidth + 1;
      int y2 = topPadding + this.fontLineHeight();
      GL11.glTranslatef((float)leftPadding, (float)topPadding, 0.0F);
      if(leftPadding <= xm && xm <= x2 && topPadding <= ym && ym <= y2) {
         this.onLink = true;
         this.drawString(text, 0, 0, 7107012);
      } else {
         this.onLink = false;
         this.drawString(text, 0, 0, 3368635);
      }

      GL11.glPopMatrix();
   }

   private void renderStage() {
      String text = "STAGE!";
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      GL11.glScalef(1.5F, 1.5F, 1.5F);
      this.drawString(text, 0, 0, -256);
      GL11.glPopMatrix();
   }

   public RealmsScreen newScreen() {
      return new RealmsMainScreen(this.lastScreen);
   }

   static {
      String version = RealmsVersion.getVersion();
      if(version != null) {
         LOGGER.info("Realms library version == " + version);
      }

   }

   private class ServerSelectionList extends RealmsScrolledSelectionList {
      public ServerSelectionList() {
         super(RealmsMainScreen.this.width(), RealmsMainScreen.this.height(), 32, RealmsMainScreen.this.height() - 64, 36);
      }

      public int getItemCount() {
         return RealmsMainScreen.trialsAvailable?RealmsMainScreen.this.realmsServers.size() + 1:RealmsMainScreen.this.realmsServers.size();
      }

      public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse) {
         if(RealmsMainScreen.trialsAvailable) {
            if(item == 0) {
               RealmsMainScreen.this.createTrial();
               return;
            }

            --item;
         }

         if(item < RealmsMainScreen.this.realmsServers.size()) {
            RealmsServer server = (RealmsServer)RealmsMainScreen.this.realmsServers.get(item);
            if(server.state == RealmsServer.State.UNINITIALIZED) {
               RealmsMainScreen.this.selectedServerId = -1L;
               RealmsMainScreen.this.stopRealmsFetcherAndPinger();
               Realms.setScreen(new RealmsCreateRealmScreen(server, RealmsMainScreen.this));
            } else {
               RealmsMainScreen.this.selectedServerId = server.id;
            }

            RealmsMainScreen.this.configureButton.active(RealmsMainScreen.overrideConfigure || RealmsMainScreen.this.isSelfOwnedServer(server) && server.state != RealmsServer.State.ADMIN_LOCK && server.state != RealmsServer.State.UNINITIALIZED);
            RealmsMainScreen.this.leaveButton.active(!RealmsMainScreen.this.isSelfOwnedServer(server));
            RealmsMainScreen.this.playButton.active(server.state == RealmsServer.State.OPEN && !server.expired);
            if(doubleClick && RealmsMainScreen.this.playButton.active()) {
               RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId));
            }

         }
      }

      public boolean isSelectedItem(int item) {
         if(RealmsMainScreen.trialsAvailable) {
            if(item == 0) {
               return false;
            }

            --item;
         }

         return item == RealmsMainScreen.this.findIndex(RealmsMainScreen.this.selectedServerId);
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground() {
         RealmsMainScreen.this.renderBackground();
      }

      protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY) {
         if(RealmsMainScreen.trialsAvailable) {
            if(i == 0) {
               this.renderTrialItem(i, x, y);
               return;
            }

            --i;
         }

         if(i < RealmsMainScreen.this.realmsServers.size()) {
            this.renderMcoServerItem(i, x, y);
         }

      }

      private void renderTrialItem(int i, int x, int y) {
         int ry = y + 12;
         int index = 0;
         String msg = RealmsScreen.getLocalizedString("mco.trial.message");
         boolean hovered = false;
         if(x <= this.xm() && this.xm() <= this.getScrollbarPosition() && y <= this.ym() && this.ym() <= y + 32) {
            hovered = true;
         }

         float scale = 0.5F + (1.0F + RealmsMth.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
         int textColor;
         if(hovered) {
            textColor = 255 | (int)(127.0F * scale) << 16 | (int)(255.0F * scale) << 8 | (int)(127.0F * scale);
         } else {
            textColor = -16777216 | (int)(127.0F * scale) << 16 | (int)(255.0F * scale) << 8 | (int)(127.0F * scale);
         }

         for(String s : msg.split("\\\\n")) {
            RealmsMainScreen.this.drawCenteredString(s, RealmsMainScreen.this.width() / 2, ry + index, textColor);
            index += 10;
         }

      }

      private void renderMcoServerItem(int i, int x, int y) {
         final RealmsServer serverData = (RealmsServer)RealmsMainScreen.this.realmsServers.get(i);
         int nameColor = RealmsMainScreen.this.isSelfOwnedServer(serverData)?8388479:16777215;
         if(serverData.state == RealmsServer.State.UNINITIALIZED) {
            RealmsScreen.bind("realms:textures/gui/realms/world_icon.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3008);
            GL11.glPushMatrix();
            RealmsScreen.blit(x + 10, y + 6, 0.0F, 0.0F, 40, 20, 40.0F, 20.0F);
            GL11.glPopMatrix();
            float scale = 0.5F + (1.0F + RealmsMth.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
            int textColor = -16777216 | (int)(127.0F * scale) << 16 | (int)(255.0F * scale) << 8 | (int)(127.0F * scale);
            RealmsMainScreen.this.drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), x + 10 + 40 + 75, y + 12, textColor);
         } else {
            if(serverData.shouldPing(Realms.currentTimeMillis())) {
               serverData.serverPing.lastPingSnapshot = Realms.currentTimeMillis();
               RealmsMainScreen.THREAD_POOL.submit(new Runnable() {
                  public void run() {
                     try {
                        RealmsMainScreen.statusPinger.pingServer(serverData.ip, serverData.serverPing);
                     } catch (UnknownHostException var2) {
                        RealmsMainScreen.LOGGER.error("Pinger: Could not resolve host");
                     }

                  }
               });
            }

            RealmsMainScreen.this.drawString(serverData.getName(), x + 2, y + 1, nameColor);
            int dx = 207;
            int dy = 1;
            if(serverData.expired) {
               RealmsMainScreen.this.drawExpired(x + dx, y + dy, this.xm(), this.ym());
            } else if(serverData.state == RealmsServer.State.CLOSED) {
               RealmsMainScreen.this.drawClose(x + dx, y + dy, this.xm(), this.ym());
            } else if(RealmsMainScreen.this.isSelfOwnedServer(serverData) && serverData.daysLeft < 7) {
               this.showStatus(x - 14, y, serverData);
               RealmsMainScreen.this.drawExpiring(x + dx, y + dy, this.xm(), this.ym(), serverData.daysLeft);
            } else if(serverData.state == RealmsServer.State.OPEN) {
               RealmsMainScreen.this.drawOpen(x + dx, y + dy, this.xm(), this.ym());
               this.showStatus(x - 14, y, serverData);
            } else if(serverData.state == RealmsServer.State.ADMIN_LOCK) {
               RealmsMainScreen.this.drawLocked(x + dx, y + dy, this.xm(), this.ym());
            }

            String noPlayers = "0";
            if(!serverData.serverPing.nrOfPlayers.equals(noPlayers)) {
               String coloredNumPlayers = ChatFormatting.GRAY + "" + serverData.serverPing.nrOfPlayers;
               RealmsMainScreen.this.drawString(coloredNumPlayers, x + 200 - RealmsMainScreen.this.fontWidth(coloredNumPlayers), y + 1, 8421504);
               if(this.xm() >= x + 200 - RealmsMainScreen.this.fontWidth(coloredNumPlayers) && this.xm() <= x + 200 && this.ym() >= y + 1 && this.ym() <= y + 9 && this.ym() < RealmsMainScreen.this.height() - 64 && this.ym() > 32) {
                  RealmsMainScreen.this.toolTip = serverData.serverPing.playerList;
               }
            }

            if(serverData.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
               int motdColor = 9206892;
               if(RealmsMainScreen.this.animTick % 10 < 5) {
                  motdColor = 13413468;
               }

               String miniGameStr = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
               int mgWidth = RealmsMainScreen.this.fontWidth(miniGameStr);
               RealmsMainScreen.this.drawString(miniGameStr, x + 2, y + 12, motdColor);
               RealmsMainScreen.this.drawString(serverData.getMinigameName(), x + 2 + mgWidth, y + 12, 7105644);
            } else {
               RealmsMainScreen.this.drawString(serverData.getDescription(), x + 2, y + 12, 7105644);
            }

            RealmsMainScreen.this.drawString(serverData.owner, x + 2, y + 12 + 11, 5000268);
            RealmsScreen.bindFace(serverData.ownerUUID, serverData.owner);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(x - 36, y, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
            RealmsScreen.blit(x - 36, y, 40.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
         }
      }

      private void showStatus(int x, int y, RealmsServer serverData) {
         if(serverData.ip != null) {
            if(serverData.status != null) {
               RealmsMainScreen.this.drawString(serverData.status, x + 215 - RealmsMainScreen.this.fontWidth(serverData.status), y + 1, 8421504);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RealmsScreen.bind("textures/gui/icons.png");
         }
      }
   }
}
