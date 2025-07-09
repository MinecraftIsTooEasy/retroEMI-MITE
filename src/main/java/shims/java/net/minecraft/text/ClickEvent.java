package shims.java.net.minecraft.text;

public class ClickEvent {
    private final Action action;
    private final String value;

    public ClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ClickEvent clickEvent = (ClickEvent)o;
            return this.action == clickEvent.action && this.value.equals(clickEvent.value);
        } else {
            return false;
        }
    }

    public String toString() {
        String var10000 = String.valueOf(this.action);
        return "ClickEvent{action=" + var10000 + ", value='" + this.value + "'}";
    }

    public int hashCode() {
        int i = this.action.hashCode();
        i = 31 * i + this.value.hashCode();
        return i;
    }

    public static enum Action {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true),
        COPY_TO_CLIPBOARD("copy_to_clipboard", true);

        private final boolean userDefinable;
        private final String name;

        private Action(final String name, final boolean userDefinable) {
            this.name = name;
            this.userDefinable = userDefinable;
        }

        public boolean isUserDefinable() {
            return this.userDefinable;
        }

        public String asString() {
            return this.name;
        }
    }
}
