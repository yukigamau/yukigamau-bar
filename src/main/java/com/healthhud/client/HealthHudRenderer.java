package com.healthhud.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HealthHudRenderer
{
	private static final int BOX_WIDTH = 91; // 为了显示 cur/max，框宽调大，这个是原版的长度
	private static final int BOX_HEIGHT = 9;
	private static int flashCounter = 0;
	private static boolean isColorRed = false;

	@SubscribeEvent
	public static void renderHealth(RenderGuiOverlayEvent.Pre event)
	{
		// 拦截原版血条
		if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id()))
		{
			event.setCanceled(true);

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
				++flashCounter;
				if (flashCounter % 32 == 0)
					isColorRed = !isColorRed;

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
}