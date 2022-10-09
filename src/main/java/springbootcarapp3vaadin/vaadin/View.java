package springbootcarapp3vaadin.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import springbootcarapp3vaadin.model.Car;
import springbootcarapp3vaadin.model.Color;
import springbootcarapp3vaadin.service.CarServiceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

@Route("cars")
public class View extends VerticalLayout {

    private CarServiceImpl carServiceImpl;

    @Autowired
    public View(CarServiceImpl carServiceImpl) {
        this.carServiceImpl = carServiceImpl;

        Grid<Car> grid = new Grid<>();

        //get all
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setItems(carServiceImpl.getAll());
        Grid.Column<Car> idColumn = grid.addColumn(Car::getCarId).setHeader("Car id");
        Grid.Column<Car> brandColumn = grid.addColumn(Car::getBrand).setHeader("Brand");
        Grid.Column<Car> modelColumn = grid.addColumn(Car::getModel).setHeader("Model");
        Grid.Column<Car> colorColumn = grid.addColumn(Car::getColor).setHeader("Color");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        // add car
        Button addButton = new Button("Add Car");
        ComboBox<Color> colorComboBox = new ComboBox<>("Choose color type", Color.values());
        TextField textFieldBrand = new TextField();
        textFieldBrand.setLabel("Add brand");
        TextField textFieldModel = new TextField("Add model");

        addButton.addClickListener(buttonClickEvent -> {
            boolean save = carServiceImpl.save(new Car(carServiceImpl.findMaxId(),
                    textFieldBrand.getValue(), textFieldModel.getValue(),
                    colorComboBox.getValue()));

            if (save) {
                Notification.show(
                        "Car added!", 3000,
                        Notification.Position.TOP_CENTER);
            } else {
                Notification.show(
                        "Sth went wrong...", 3000,
                        Notification.Position.TOP_CENTER);
            }
            grid.getDataProvider().refreshAll();
        });

        ListDataProvider<Car> dataProvider = new ListDataProvider<>(carServiceImpl.getAll());
        grid.setDataProvider(dataProvider);

        //find by ID
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        TextField idFieldFilter = new TextField();
        idFieldFilter.addValueChangeListener(event -> dataProvider.addFilter(
                car -> StringUtils.containsIgnoreCase(Long.toString(car.getCarId()),
                        idFieldFilter.getValue())));
        idFieldFilter.setValueChangeMode(ValueChangeMode.EAGER);

        FooterRow filterRow = grid.appendFooterRow();
        filterRow.getCell(idColumn).setComponent(idFieldFilter);
        idFieldFilter.setSizeFull();
        idFieldFilter.setPlaceholder("id filter");

        ComboBox<Color> colorSearch = new ComboBox<>("Color type", Color.values());
        colorSearch.addValueChangeListener(event -> {
            grid.setItems(carServiceImpl.carByColor(String.valueOf(colorSearch.getValue())));
            add(grid);
        });
        add(colorSearch);

        //delete
        grid.addComponentColumn(item -> createRemoveButton(grid, item))
                .setHeader("Remove");
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        //edytowanie
        Binder<Car> binder = new Binder<>(Car.class);
        Editor<Car> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Div validationStatus = new Div();
        validationStatus.setId("validation");

        TextField brandField = new TextField();
        binder.forField(brandField).bind("brand");
        brandColumn.setEditorComponent(brandField);

        TextField modelField = new TextField();
        binder.forField(modelField).bind("model");
        modelColumn.setEditorComponent(modelField);

//        ComboBox<Color> colorComboBox2 = new ComboBox<>("Color type", Color.values());
        ComboBox colorComboBox2 = new ComboBox(String.valueOf(Color.class), Color.values());
        binder.forField(colorComboBox2).bind("color");
        colorColumn.setEditorComponent(colorComboBox2);

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<Car> editorColumn = grid.addComponentColumn(car -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(car);
                brandField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        FooterRow footerRow = grid.appendFooterRow();

        footerRow.getCell(brandColumn).setComponent(textFieldBrand);
        footerRow.getCell(modelColumn).setComponent(textFieldModel);
        footerRow.getCell(colorColumn).setComponent(colorComboBox);
        footerRow.getCell(editorColumn).setComponent(addButton);

        add(grid);
    }
    private Button createRemoveButton(Grid<Car> grid, Car item) {
        @SuppressWarnings("unchecked")
        Button button = new Button("Remove", clickEvent -> {
            ListDataProvider<Car> dataProvider = (ListDataProvider<Car>) grid
                    .getDataProvider();
            dataProvider.getItems().remove(item);
            dataProvider.refreshAll();
        });
        return button;
    }
}

