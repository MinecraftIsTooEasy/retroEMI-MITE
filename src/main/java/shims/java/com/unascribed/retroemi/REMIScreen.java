package shims.java.com.unascribed.retroemi;

import com.google.common.collect.Lists;
import dev.emi.emi.input.EmiInput;
import shims.java.net.minecraft.client.gui.DrawContext;
import shims.java.net.minecraft.client.gui.Drawable;
import shims.java.net.minecraft.client.gui.Element;
import shims.java.net.minecraft.client.gui.ParentElement;
import shims.java.net.minecraft.text.Text;
import shims.java.org.lwjgl.glfw.GLFW;
import net.minecraft.GuiScreen;
import net.minecraft.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;

public class REMIScreen extends GuiScreen implements ParentElement {
	
	protected final Minecraft client = Minecraft.getMinecraft();
	private int mouseDown = -1;
	private int lastMouseX, lastMouseY;
	
	private boolean closing = false;
	
	protected final Text title;
	private final List<Element> children = Lists.newArrayList();
	private final List<Drawable> drawables = Lists.newArrayList();
	
	public REMIScreen(Text title) {
		this.title = title;
	}
	
	@Override
	public void initGui() {
		closing = false;
		mouseDown = -1;
		super.initGui();
		children.clear();
		drawables.clear();
		init();
	}
	
	@Override
	public final void drawScreen(int mouseX, int mouseY, float delta) {
		glEnable(GL_RESCALE_NORMAL);
		render(DrawContext.INSTANCE, mouseX, mouseY, delta);
		super.drawScreen(mouseX, mouseY, delta);
		if (mouseDown != -1) {
			mouseDragged(mouseX, mouseY, mouseDown, mouseX - lastMouseX, mouseY - lastMouseY);
		}
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		glDisable(GL_RESCALE_NORMAL);
	}
	
	@Override
	public void handleKeyboardInput() {
		super.handleKeyboardInput();
		int k = Keyboard.getEventKey();
		char c = Keyboard.getEventCharacter();
		if (!Keyboard.getEventKeyState() || k == 0 && Character.isDefined(c)) {
			keyReleased(Keyboard.getEventKey(), 0, EmiInput.getCurrentModifiers());
		}
	}
	
	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		if (Mouse.getEventDWheel() != 0) {
			mouseScrolled(lastMouseX, lastMouseY, Mouse.getEventDWheel() / 120D);
		}
	}
	
	@Override
	public final void keyTyped(char character, int keyCode) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc()) {
			close();
			return;
		}
		int mod = EmiInput.getCurrentModifiers();
		keyPressed(keyCode, 0, mod);
		if (character != 0 && !Character.isISOControl(character)) {
			charTyped(character, mod);
		}
	}
	
	@Override
	public final void mouseClicked(int mouseX, int mouseY, int button) {
		mouseClicked((double) mouseX, (double) mouseY, button);
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		mouseDown = button;
	}
	
	@Override
	public final void mouseMovedOrUp(int mouseX, int mouseY, int button) {
		mouseReleased((double) mouseX, (double) mouseY, button);
		if (button == mouseDown) {
			mouseDown = -1;
		}
	}
	
	@Override
	public final boolean doesGuiPauseGame() {
		return shouldPause();
	}
	
	@Override
	public final void onGuiClosed() {
		mouseDown = -1;
		if (closing) {
			return;
		}
		closing = true;
		close();
	}
	
	
	protected boolean shouldCloseOnEsc() {
		return true;
	}
	
	protected void init() {
		
	}
	
	protected boolean shouldPause() {
		return super.doesGuiPauseGame();
	}
	
	protected void render(DrawContext raw, int mouseX, int mouseY, float delta) {
		for (Drawable drawable : this.drawables) {
			drawable.render(raw, mouseX, mouseY, delta);
		}
	}
	
	protected void renderBackgroundTexture(DrawContext ctx) {
		drawBackground(0);
	}
	
	protected void renderBackground(DrawContext ctx) {
		drawDefaultBackground();
	}
	
	protected void close() {
		super.onGuiClosed();
		client.displayGuiScreen(null);
	}
	
	
	@Nullable
	private Element focused;
	private boolean dragging;
	
	@Override
	public final boolean isDragging() {
		return this.dragging;
	}
	
	@Override
	public final void setDragging(boolean dragging) {
		this.dragging = dragging;
	}
	
	@Override
	@Nullable
	public Element getFocused() {
		return this.focused;
	}
	
	@Override
	public void setFocused(@Nullable Element focused) {
		if (this.focused != null) {
			this.focused.setFocused(false);
		}
		if (focused != null) {
			focused.setFocused(true);
		}
		this.focused = focused;
	}
	
	
	@Override
	public List<? extends Element> children() {
		return children;
	}
	
	
	protected <T extends Element & Drawable> T addDrawableChild(T drawableElement) {
		this.drawables.add(drawableElement);
		return this.addSelectableChild(drawableElement);
	}
	
	protected <T extends Drawable> T addDrawable(T drawable) {
		this.drawables.add(drawable);
		return drawable;
	}
	
	protected <T extends Element> T addSelectableChild(T child) {
		this.children.add(child);
		return child;
	}
	
	protected void remove(Element child) {
		if (child instanceof Drawable) {
			this.drawables.remove((Drawable) child);
		}
		this.children.remove(child);
	}
}
