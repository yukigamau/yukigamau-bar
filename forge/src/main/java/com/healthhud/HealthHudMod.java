// 文件: src/main/java/com/healthhud/HealthHudMod.java
package com.healthhud;

import net.minecraftforge.fml.common.Mod;

@Mod(HealthHudMod.MODID)
public class HealthHudMod
{
	public static final String MODID = "yukigamau_bar";
	// 主类无需额外注册 HealthHudRenderer，@Mod.EventBusSubscriber 会自动注册
}