public class DependantFilter implements Filter {
    private final String startingSequence;
    private final String endingSequence;
    private final String dependencyName;
    private BackusChecker dependency;

    public DependantFilter(String startingSequence, String endingSequence, String dependencyName) {
        this.startingSequence = startingSequence;
        this.endingSequence = endingSequence;
        this.dependencyName = dependencyName;
    }

    public void setDependency(BackusChecker dependency) {
        this.dependency = dependency;
    }

    @Override
    public boolean isSafetyCheck() {
        return false;
    }

    @Override
    public boolean check(String stringToCheck) {
        if (stringToCheck.substring(0, startingSequence.length()).equals(startingSequence)
                && stringToCheck.substring(stringToCheck.length() - endingSequence.length())
                .equals(endingSequence)) {

            if (dependency == null) {
                throw new IllegalStateException("Dependency " + dependencyName + " not added in " + "");
            }

            return dependency.check(stringToCheck.substring(startingSequence.length(),
                    stringToCheck.length() - endingSequence.length()));
        } else {
            return false;
        }
    }

    public String getDependencyName() {
        return dependencyName.substring(1, dependencyName.length() - 1);
    }
}
