package ui;

public class StyleUtil {

   
    public static final String BG_PAGE      = "#F7F5F0";   // warm off-white page
    public static final String BG_SURFACE   = "#FFFFFF";   // pure white surfaces/cards
    public static final String BG_SIDEBAR   = "#1C2B3A";   // deep navy sidebar
    public static final String BG_INPUT     = "#F0EDE8";   // warm input fill

    public static final String TEXT_DARK    = "#1A1A1A";   // near-black body text
    public static final String TEXT_MUTED   = "#6B6B6B";   // secondary text
    public static final String TEXT_SIDEBAR = "#B8C5D0";   // sidebar muted text
    public static final String TEXT_SIDEBAR_ACTIVE = "#FFFFFF";

    public static final String ACCENT_TEAL  = "#0F7B6C";   // primary action - deep teal
    public static final String ACCENT_TEAL_LIGHT = "#E1F5EE"; // teal background tint
    public static final String ACCENT_AMBER = "#B07A0A";   // warning - deep amber
    public static final String ACCENT_AMBER_LIGHT = "#FEF6E4";
    public static final String ACCENT_RED   = "#A32D2D";   // danger - deep red
    public static final String ACCENT_RED_LIGHT = "#FCEBEB";
    public static final String ACCENT_BLUE  = "#185FA5";   // info - deep blue
    public static final String ACCENT_BLUE_LIGHT = "#E6F1FB";

    public static final String BORDER       = "#E2DDD6";   // warm border
    public static final String BORDER_DARK  = "#C8C2BA";   // stronger border

    
    public static final String FONT_SIZE_XS = "11px";
    public static final String FONT_SIZE_SM = "12px";
    public static final String FONT_SIZE_MD = "13px";
    public static final String FONT_SIZE_LG = "15px";
    public static final String FONT_SIZE_XL = "18px";
    public static final String FONT_SIZE_2XL = "24px";

   
    public static String card() {
        return "-fx-background-color: " + BG_SURFACE + ";" +
               "-fx-background-radius: 10;" +
               "-fx-border-color: " + BORDER + ";" +
               "-fx-border-radius: 10;" +
               "-fx-border-width: 1;";
    }

    public static String inputField() {
        return "-fx-background-color: " + BG_INPUT + ";" +
               "-fx-text-fill: " + TEXT_DARK + ";" +
               "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
               "-fx-border-color: " + BORDER_DARK + ";" +
               "-fx-border-radius: 6;" +
               "-fx-background-radius: 6;" +
               "-fx-border-width: 1;" +
               "-fx-padding: 9 12;" +
               "-fx-font-size: 15px;";
    }

    public static String btnPrimary() {
        return "-fx-background-color: " + ACCENT_TEAL + ";" +
               "-fx-text-fill: #FFFFFF;" +
               "-fx-font-weight: bold;" +
               "-fx-font-size: 15px;" +
               "-fx-padding: 10 22;" +
               "-fx-background-radius: 7;" +
               "-fx-cursor: hand;";
    }

    public static String btnSecondary() {
        return "-fx-background-color: " + BG_SURFACE + ";" +
               "-fx-text-fill: " + TEXT_DARK + ";" +
               "-fx-font-size: 15px;" +
               "-fx-padding: 9 20;" +
               "-fx-background-radius: 7;" +
               "-fx-border-color: " + BORDER_DARK + ";" +
               "-fx-border-radius: 7;" +
               "-fx-border-width: 1;" +
               "-fx-cursor: hand;";
    }

    public static String btnDanger() {
        return "-fx-background-color: " + ACCENT_RED + ";" +
               "-fx-text-fill: #FFFFFF;" +
               "-fx-font-weight: bold;" +
               "-fx-font-size: 15px;" +
               "-fx-padding: 7 16;" +
               "-fx-background-radius: 7;" +
               "-fx-cursor: hand;";
    }

    public static String btnAmber() {
        return "-fx-background-color: " + ACCENT_AMBER + ";" +
               "-fx-text-fill: #FFFFFF;" +
               "-fx-font-weight: bold;" +
               "-fx-font-size: 15px;" +
               "-fx-padding: 7 16;" +
               "-fx-background-radius: 7;" +
               "-fx-cursor: hand;";
    }

    public static String badge(String bgColor, String textColor) {
        return "-fx-background-color: " + bgColor + ";" +
               "-fx-text-fill: " + textColor + ";" +
               "-fx-font-size: 15px;" +
               "-fx-font-weight: bold;" +
               "-fx-padding: 3 8;" +
               "-fx-background-radius: 4;";
    }

   
    public static String navBtnActive() {
        return "-fx-background-color: rgba(255,255,255,0.12);" +
               "-fx-text-fill: " + TEXT_SIDEBAR_ACTIVE + ";" +
               "-fx-font-size: 15px;" +
               "-fx-padding: 10 16;" +
               "-fx-background-radius: 8;" +
               "-fx-alignment: center-left;" +
               "-fx-cursor: hand;" +
               "-fx-border-color: rgba(255,255,255,0.2);" +
               "-fx-border-radius: 8;" +
               "-fx-border-width: 1;";
    }

    public static String navBtnIdle() {
        return "-fx-background-color: transparent;" +
               "-fx-text-fill: " + TEXT_SIDEBAR + ";" +
               "-fx-font-size: 15px;" +
               "-fx-padding: 10 16;" +
               "-fx-background-radius: 8;" +
               "-fx-alignment: center-left;" +
               "-fx-cursor: hand;";
    }

    public static String navBtnHover() {
        return "-fx-background-color: rgba(255,255,255,0.07);" +
               "-fx-text-fill: #FFFFFF;" +
               "-fx-font-size: 15px;" +
               "-fx-padding: 10 16;" +
               "-fx-background-radius: 8;" +
               "-fx-alignment: center-left;" +
               "-fx-cursor: hand;";
    }

    
    public static String formatRupiah(int amount) {
        return "Rp " + String.format("%,d", amount).replace(",", ".");
    }

    public static String label(String size, String color) {
        return "-fx-font-size: " + size + "; -fx-text-fill: " + color + ";";
    }
}
