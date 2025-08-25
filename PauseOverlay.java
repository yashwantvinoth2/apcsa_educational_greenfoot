import greenfoot.*;      // GreenfootImage, Actor, Color, etc.
import java.util.ArrayList;
import java.util.List;

/**
 * Full-screen pause overlay: dims background, shows a centered semi-transparent panel,
 * a title, a wrapped fact, a lives line, and a prompt.
 */
public class PauseOverlay extends Actor {

    private final int worldW, worldH;

    private final String title;
    private final String fact;
    private final String livesLine;
    private final String prompt;

    // Layout
    private final int panelPadding = 20;
    private final int lineSpacing  = 24;     // spacing between wrapped fact lines
    private final int sectionGap   = 12;     // spacing between sections
    private final int cornerRadius = 18;

    // Font sizes
    private final int titleSize  = 28;
    private final int bodySize   = 18;
    private final int promptSize = 18;

    // Colors (use greenfoot.Color, not java.awt.Color)
    private final Color textColor  = new Color(255, 255, 255, 245);
    private final int   dimAlpha   = 120;               // transparency via image
    private final Color panelSolid = new Color(20, 20, 20); // panel base (we'll set image transparency)

    private final int panelWidth;

    public PauseOverlay(int worldW, int worldH,
                        String title, String fact, String livesLine, String prompt) {
        this.worldW = worldW;
        this.worldH = worldH;
        this.title = title;
        this.fact = fact;
        this.livesLine = livesLine;
        this.prompt = prompt;

        int target = (int)(worldW * 0.7);
        this.panelWidth = clamp(target, 500, Math.max(520, worldW - 80));

        setImage(buildImage());
    }

    private GreenfootImage buildImage() {
        GreenfootImage canvas = new GreenfootImage(worldW, worldH);

        // 1) Dim entire screen (draw on a separate image so we can set transparency safely)
        GreenfootImage dim = new GreenfootImage(worldW, worldH);
        dim.setColor(new Color(0, 0, 0));
        dim.fillRect(0, 0, worldW, worldH);
        dim.setTransparency(dimAlpha);
        canvas.drawImage(dim, 0, 0);

        // 2) Wrap fact lines to panel width
        int textMaxWidth = panelWidth - (panelPadding * 2);
        List<String> factLines = wrapByPixelWidth(fact, bodySize, textMaxWidth);

        // 3) Measure title/prompt heights using text images (no AWT metrics)
        GreenfootImage titleImg  = new GreenfootImage(title,  titleSize,  textColor, new Color(0,0,0,0));
        GreenfootImage livesImg  = new GreenfootImage(livesLine, bodySize, textColor, new Color(0,0,0,0));
        GreenfootImage promptImg = new GreenfootImage(prompt, promptSize, textColor, new Color(0,0,0,0));

        int titleH  = titleImg.getHeight();
        int wrappedH = factLines.size() * lineSpacing;
        int livesH  = livesImg.getHeight();
        int promptH = promptImg.getHeight();

        int panelHeight = panelPadding
                        + titleH
                        + sectionGap
                        + wrappedH
                        + sectionGap
                        + livesH
                        + sectionGap
                        + promptH
                        + panelPadding;

        int panelX = (worldW - panelWidth) / 2;
        int panelY = (worldH - panelHeight) / 2;

        // 4) Draw rounded panel on its own image, then apply transparency
        GreenfootImage panel = new GreenfootImage(panelWidth, panelHeight);
        panel.setColor(panelSolid);
        fillRoundRect(panel, 0, 0, panelWidth, panelHeight, cornerRadius);
        panel.setTransparency(200); // semi-transparent panel
        canvas.drawImage(panel, panelX, panelY);

        // 5) Draw text centered
        int centerX = worldW / 2;
        int y = panelY + panelPadding;

        drawCentered(canvas, titleImg, centerX, y);
        y += titleH + sectionGap;

        for (String line : factLines) {
            GreenfootImage lineImg = new GreenfootImage(line, bodySize, textColor, new Color(0,0,0,0));
            drawCentered(canvas, lineImg, centerX, y);
            y += lineSpacing;
        }

        y += sectionGap;
        drawCentered(canvas, livesImg, centerX, y);
        y += livesH + sectionGap;
        drawCentered(canvas, promptImg, centerX, y);

        return canvas;
    }

    private void drawCentered(GreenfootImage canvas, GreenfootImage img, int centerX, int topY) {
        int drawX = centerX - (img.getWidth() / 2);
        canvas.drawImage(img, drawX, topY);
    }

    /** Wrap text by pixel width using GreenfootImage width (no FontMetrics). */
    private List<String> wrapByPixelWidth(String text, int fontSize, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.trim().split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String w : words) {
            String candidate = current.length() == 0 ? w : current + " " + w;
            int candW = new GreenfootImage(candidate, fontSize, textColor, new Color(0,0,0,0)).getWidth();
            if (candW <= maxWidth) {
                current.setLength(0);
                current.append(candidate);
            } else {
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current.setLength(0);
                    current.append(w);
                } else {
                    // very long single word: hard break (fallback)
                    lines.add(w);
                }
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        return lines;
    }

    /** Rounded rectangle fill using ovals + rects (portable across Greenfoot versions). */
    private void fillRoundRect(GreenfootImage img, int x, int y, int w, int h, int r) {
        img.fillRect(x + r, y, w - 2*r, h);
        img.fillRect(x, y + r, w, h - 2*r);
        img.fillOval(x, y, 2*r, 2*r);
        img.fillOval(x + w - 2*r, y, 2*r, 2*r);
        img.fillOval(x, y + h - 2*r, 2*r, 2*r);
        img.fillOval(x + w - 2*r, y + h - 2*r, 2*r, 2*r);
    }

    private int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
