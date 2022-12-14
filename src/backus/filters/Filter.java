package backus.filters;

public interface Filter {
    /* Ако филтър връща true, то отрицателен отговор от неговата проверка следва да води до веднагически отрицателен отговор
     противно на нормалните филтри, където позитививен отговор на неговата проверка води до веднагически положителен отговор*/
    boolean isSafetyCheck();
    boolean check(String stringToCheck);
}
