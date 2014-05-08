package fr.cm.common.widget.text;

public interface TextModifyListener<T> {

    void onModify(String value);

    void onExit();

    void onEntry();
}
