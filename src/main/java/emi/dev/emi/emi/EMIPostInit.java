package emi.dev.emi.emi;


import emi.dev.emi.emi.data.EmiData;
import emi.dev.emi.emi.network.*;
import emi.dev.emi.emi.platform.EmiClient;
import emi.dev.emi.emi.platform.EmiMain;
import emi.mitemod.emi.api.EMIPlayerControllerMP;
import emi.mitemod.emi.util.MinecraftServerEMI;
import emi.shims.java.com.unascribed.retroemi.EmiAgnosMITEFish;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.network.PacketByteBuf;
import net.fabricmc.api.ModInitializer;
import net.minecraft.Minecraft;
import net.minecraft.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class EMIPostInit implements ModInitializer {

	private static boolean isEMIInit = false;
	@Override
	public void onInitialize() {

	}

	public static void initEMI() {
		if (!isEMIInit) {
			InRelauncher.init();
			isEMIInit = true;
		}
	}

	public static final class InRelauncher {
		
		public static void init() {
			EmiAgnosMITEFish.poke();
			if (!MinecraftServerEMI.getIsServer()) {
				Client.init();
			}
//			else {
//				Server.init();
//			}
			EmiMain.init();
			
			EmiNetwork.initServer((player, packet) -> {
				player.playerNetServerHandler.sendPacketToPlayer(toVanilla(packet));
			});
			
			PacketReader.registerServerPacketReader(EmiNetwork.FILL_RECIPE, FillRecipeC2SPacket::new);
			PacketReader.registerServerPacketReader(EmiNetwork.CREATE_ITEM, CreateItemC2SPacket::new);
			PacketReader.registerServerPacketReader(EmiNetwork.CHESS, EmiChessPacket.C2S::new);
		}
		
		
		/*
                    NetworkRegistry.instance().registerConnectionHandler(new IConnectionHandler() {
                        @Override
                        public void playerLoggedIn(Player var1, NetHandler var2, INetworkManager var3) {
                            if (var1 instanceof EntityPlayerMP esp) {
                                EmiNetwork.sendToClient(esp, new PingS2CPacket());
                            }
                        }

                        @Override public void clientLoggedIn(NetHandler var1, INetworkManager var2, Packet1Login var3) {}
                        @Override public void connectionClosed(INetworkManager var1) {}
                        @Override public String connectionReceived(NetLoginHandler var1, INetworkManager var2) { return null; }
                        @Override public void connectionOpened(NetHandler var1, String var2, int var3, INetworkManager var4) {}
                        @Override public void connectionOpened(NetHandler var1, MinecraftServer var2, INetworkManager var3) {}
                    });
                }
                */
		private static Packet250CustomPayload toVanilla(EmiPacket packet) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			PacketByteBuf buf = PacketByteBuf.out(dos);
			packet.write(buf);
			Packet250CustomPayload pkt = new Packet250CustomPayload(RetroEMI.compactify(packet.getId()), baos.toByteArray());
			return pkt;
		}
		
		public static final class Client {
			
			public static void init() {
				EmiClient.init();
				EmiData.init();
				
				EmiNetwork.initClient(packet -> ((EMIPlayerControllerMP) Minecraft.getMinecraft().playerController).getNetClientHandler().addToSendQueue(toVanilla(packet)));
				PacketReader.registerClientPacketReader(EmiNetwork.PING, PingS2CPacket::new);
				//NYI
				//PacketReader.registerClientPacketReader(EmiNetwork.COMMAND, CommandS2CPacket::new);
				PacketReader.registerClientPacketReader(EmiNetwork.CHESS, EmiChessPacket.S2C::new);
			}
			
		}
		
//		public static final class Server {
//
//			public static void init() {
//				EmiData.init(ResourceReloader::reload);
//			}
//		}
	}
}
