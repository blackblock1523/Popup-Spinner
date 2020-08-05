
# Popup-Spinner
## Gradle
```
allprojects {
    repositories {
    	...
    	maven { url 'https://jitpack.io' }
    }
}
```

```
dependencies {
    implementation 'com.github.blackblock1523:Popup-Spinner:v1.0'
}
```

## How to use

#### in xml layout:
```
    <com.blackblock.popupSpinner.view.PopupWindowSpinner
        android:id="@+id/nice_spinner"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```

+ Note: change layout_width to at least the width of the largest item on the list to prevent resizing
 
#### set datasource
```
    List<String> items = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
    // List<String> values = new LinkedList<>(Arrays.asList("1", "2", "3", "4", "5"));
    spinner.setData(items);
    // spinner.setData(items, values);
    spinner.setSelection(0, false);
```

#### listener

```
spinner.setOnItemSelectedListener(new PopupWindowSpinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(PopupWindowSpinner sp, int position) {
                String selectItem = sp.getSelectItem();
                String selectValue = sp.getSelectValue();
                ...
            }
        });
```

#### Attributes
You can add attributes to customize the view. Available attributes:

| name                      | type      | info                                                   |
|------------------------   |-----------|--------------------------------------------------------|
| isHideSelectItem          | boolean   | set whether show or hide the selectItem                |
| hideArrow                 | boolean   | set whether show or hide the drop-down arrow           |
| arrowTint                 | color     | set the drawable color                                 |
| arrowDrawable             | reference | set the drawable of the drop-down arrow                |
| textTint                  | color     | set the text color                                     |
| dropDownListPaddingBottom | dimension | set the bottom padding of the drop-down list           |
| backgroundSelector        | integer   | set the background selector for the drop-down list rows|
| entries                   | reference | set the data source from an array of strings           |
