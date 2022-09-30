import java.util.Objects;

public class Definition {

    private final String definition;

    public Definition(String definition) {
        this.definition = definition;
    }

    public String getFullDefinition() {
        return definition;
    }

    public String getStrippedDefinition() {
        return definition.substring(1, definition.length() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Definition that = (Definition) o;
        return Objects.equals(definition, that.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(definition);
    }

    public static Definition fromName(String name) {
        return new Definition("<" + name + ">");
    }
}
