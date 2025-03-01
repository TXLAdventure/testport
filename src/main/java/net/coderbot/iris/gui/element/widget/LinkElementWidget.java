package net.coderbot.iris.gui.element.widget;

import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;

import net.coderbot.iris.gui.GuiUtil;
import net.coderbot.iris.gui.NavigationController;
import net.coderbot.iris.gui.screen.ShaderPackScreen;
import net.coderbot.iris.shaderpack.option.menu.OptionMenuLinkElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class LinkElementWidget extends CommentedElementWidget<OptionMenuLinkElement> {
	private static final Component ARROW = new TextComponent(">");

	private final String targetScreenId;
	private final MutableComponent label;

	private NavigationController navigation;
	private MutableComponent trimmedLabel = null;
	private boolean isLabelTrimmed = false;

	public LinkElementWidget(OptionMenuLinkElement element) {
		super(element);

		this.targetScreenId = element.targetScreenId;
		this.label = GuiUtil.translateOrDefault(new TextComponent(element.targetScreenId), "screen." + element.targetScreenId);
	}

	@Override
	public void init(ShaderPackScreen screen, NavigationController navigation) {
		this.navigation = navigation;
	}

	@Override
	public void render(PoseStack poseStack, int x, int y, int width, int height, int mouseX, int mouseY, float tickDelta, boolean hovered) {
		GuiUtil.bindIrisWidgetsTexture();
		GuiUtil.drawButton(poseStack, x, y, width, height, hovered, false);

		Font font = Minecraft.getInstance().font;

		int maxLabelWidth = width - 9;

		if (font.width(this.label) > maxLabelWidth) {
			this.isLabelTrimmed = true;
		}

		if (this.trimmedLabel == null) {
			this.trimmedLabel = GuiUtil.shortenText(font, this.label, maxLabelWidth);
		}

		int labelWidth = font.width(this.trimmedLabel);

		font.drawShadow(poseStack, this.trimmedLabel, x + (int)(width * 0.5) - (int)(labelWidth * 0.5) - (int)(0.5 * Math.max(labelWidth - (width - 18), 0)), y + 7, 0xFFFFFF);
		font.draw(poseStack, ARROW, (x + width) - 9, y + 7, 0xFFFFFF);

		if (hovered && this.isLabelTrimmed) {
			// To prevent other elements from being drawn on top of the tooltip
			ShaderPackScreen.TOP_LAYER_RENDER_QUEUE.add(() -> GuiUtil.drawTextPanel(font, poseStack, this.label, mouseX + 2, mouseY - 16));
		}
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
			this.navigation.open(targetScreenId);
			GuiUtil.playButtonClickSound();

			return true;
		}
		return super.mouseClicked(mx, my, button);
	}

	@Override
	public Optional<Component> getCommentTitle() {
		return Optional.of(this.label);
	}

	@Override
	public Optional<Component> getCommentBody() {
		String translation = "screen." + this.targetScreenId + ".comment";
		return Optional.ofNullable(I18n.exists(translation) ? new TranslatableComponent(translation) : null);
	}
}
