//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.GuiGraphics;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(Gui.class)
//public class GuiMixin {
//
//	@Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
//	private void cancelHearts(GuiGraphics guiGraphics, CallbackInfo ci) {
//		ci.cancel();
//	}
//}