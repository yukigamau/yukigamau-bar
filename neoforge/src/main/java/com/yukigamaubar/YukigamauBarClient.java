package com.yukigamaubar;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.player.Player;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = YukigamauBar.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = YukigamauBar.MODID, value = Dist.CLIENT)
public class YukigamauBarClient {
    public YukigamauBarClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        YukigamauBar.LOGGER.info("HELLO FROM CLIENT SETUP");
        YukigamauBar.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

	/* 用于下面的血条的绘制 */
	private static final int BOX_WIDTH = 80; // 为了显示 cur/max，框宽调大，这个是原版的长度
	private static final int BOX_HEIGHT = 9;
	private static boolean isColorRed = false;
	private static long lastTick = -1;

	@SubscribeEvent
	public static void render(RenderGuiLayerEvent.Pre event) {
		if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) {
			event.setCanceled(true);
		}

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) return;

		// 只在生存、冒险模式出现
		if (player.isCreative() || player.isSpectator())
			return;

		GuiGraphics gui = event.getGuiGraphics();
		Font font = mc.font;

		int width = mc.getWindow().getGuiScaledWidth();
		int height = mc.getWindow().getGuiScaledHeight();

		int x = width / 2 - 91;
		int y = height - 39;

		float health = player.getHealth();
		float max = player.getMaxHealth();
		float absort=player.getAbsorptionAmount();

		// 颜色选择
		int color;
		if (health >= max)
			color = 0xFF00AA00; // 满血绿色
		else if (health > max / 2f)
			color = 0xFF0088FF; // 半血蓝
		else if (health > max * 0.2f || health > 10f)
			color = 0xFF440125; // 低血古铜紫
		else                    // 低血闪烁
		{
			// 用tick代替之前的counter，来让显示效果差不多
			long tick = mc.level.getGameTime();
			if(tick != lastTick)
			{
				lastTick = tick;
				if(lastTick % 10 == 0)
					isColorRed = !isColorRed;
			}

			if (isColorRed)
				color = 0xFFFF0000; // 闪烁红
			else
				color = 0xFF440125;    // 古铜紫
		}

		// 绘制血量方块
		if(absort > 0)
		{
			int addColor=0x88FFD700;
			gui.fillGradient(x, y, x + BOX_WIDTH, y + BOX_HEIGHT, color, addColor);
		}
		else
			gui.fill(x, y, x + BOX_WIDTH, y + BOX_HEIGHT, color);

		// 绘制血量文字 cur/max
		String text;
		if(absort > 0)
			text=((int)Math.ceil(health))+"/"+((int)Math.ceil(max))+"+"+((int)Math.ceil(absort));
		else
			text = ((int) Math.ceil(health)) + "/" + ((int) Math.ceil(max));
		int textX = x + BOX_WIDTH / 2 - font.width(text) / 2;
		int textY = y + 1;

		gui.drawString(font, text, textX, textY, 0xFFFFFFFF, false);
	}
}
