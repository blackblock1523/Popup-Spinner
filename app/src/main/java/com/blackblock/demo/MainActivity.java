package com.blackblock.demo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blackblock.popupSpinner.util.ISimpleFormat;
import com.blackblock.popupSpinner.view.PopupWindowSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> items = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
    List<String> values = new LinkedList<>(Arrays.asList("1", "2", "3", "4", "5"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDefaultConfig();
        initHideSelectedItem();
        initCustomObj();
        initXML();
    }

    private void initDefaultConfig() {
        PopupWindowSpinner spinner = findViewById(R.id.nice_spinner);
        spinner.setData(items, values);
        spinner.setSelection(0, false);

        spinner.setOnItemSelectedListener(new PopupWindowSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(PopupWindowSpinner sp, int position) {
                Toast.makeText(MainActivity.this, String.format("selectItem: %s, selectValue: %s",
                        sp.getSelectItem(), sp.getSelectValue()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initHideSelectedItem() {
        PopupWindowSpinner spinner1 = findViewById(R.id.nice_spinner1);
        spinner1.setData(items, values);
        spinner1.setSelection(0, false);

        spinner1.setOnItemSelectedListener(new PopupWindowSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(PopupWindowSpinner sp, int position) {
                Toast.makeText(MainActivity.this, String.format("selectItem: %s, selectValue: %s",
                        sp.getSelectItem(), sp.getSelectValue()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCustomObj() {
        List<Object> pList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Person p = new Person(values.get(i), 18 + i, items.get(i));
            pList.add(p);
        }
        PopupWindowSpinner spinner2 = findViewById(R.id.nice_spinner2);

        spinner2.setData(pList, new ISimpleFormat() {
            @NotNull
            @Override
            public List<String> format(@NotNull List<Object> t) {
                List<String> sList = new ArrayList<>();
                for (Object obj : t) {
                    Person person = (Person) obj;
                    sList.add(person.getName() + " -> " + person.getAge());
                }
                return sList;
            }
        }, new ISimpleFormat() {
            @NotNull
            @Override
            public List<String> format(@NotNull List<Object> t) {
                List<String> sList = new ArrayList<>();
                for (Object obj : t) {
                    Person person = (Person) obj;
                    sList.add(person.getId());
                }
                return sList;
            }
        });

        spinner2.setSelection(0, false);

        spinner2.setOnItemSelectedListener(new PopupWindowSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(PopupWindowSpinner sp, int position) {
                Toast.makeText(MainActivity.this, String.format("selectItem: %s, selectValue: %s",
                        sp.getSelectItem(), sp.getSelectValue()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initXML() {
        PopupWindowSpinner spinner3 = findViewById(R.id.nice_spinner3);
        spinner3.setSelection(0, true);
        spinner3.setOnItemSelectedListener(new PopupWindowSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(PopupWindowSpinner sp, int position) {
                Toast.makeText(MainActivity.this, String.format("selectItem: %s, selectValue: %s",
                        sp.getSelectItem(), sp.getSelectValue()), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
