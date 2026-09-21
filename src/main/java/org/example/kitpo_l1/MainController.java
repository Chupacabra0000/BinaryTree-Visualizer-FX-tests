package org.example.kitpo_l1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.util.List;

public class MainController {

    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField inputField;
    @FXML private Button addBtn;
    @FXML private Button removeBtn;
    @FXML private Button balanceBtn;
    @FXML private Button saveBtn;
    @FXML private Button loadBtn;
    @FXML private ListView<String> treeView;
    @FXML private Label statusLabel;
    @FXML private TextField indexField;
    @FXML private Button insertAtBtn;


    // Новое: панель для рисования дерева
    @FXML private TreeViewPane treeCanvas;

    private final UserFactory factory = new UserFactory();
    private UserType currentType;
    private BinaryTree tree;

    @FXML
    public void initialize() {
        // Заполнить ComboBox доступными типами
        typeComboBox.getItems().addAll(factory.getTypeNameList());
        if (!typeComboBox.getItems().isEmpty()) {
            typeComboBox.getSelectionModel().select(0);
            selectType(typeComboBox.getValue());
        }

        // Обработчик смены типа
        typeComboBox.setOnAction(e -> selectType(typeComboBox.getValue()));

        // Двойной клик по элементу — перейти к удалению/выбору
        treeView.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                int idx = treeView.getSelectionModel().getSelectedIndex();
                if (idx >= 0) {
                    // подсказка — выделено, можно удалить по кнопке
                    status("Selected index " + idx);
                }
            }
        });

        // Клик по панели дерева — показать выбранный узел (если попадание)
        treeCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            TreeViewPane.HitInfo info = treeCanvas.hitTest((int) ev.getX(), (int) ev.getY());
            if (info != null && info.node != null) {
                status("Нажат узел: " + info.node.toString());
            }
        });

        status("Готово.");
    }

    private void selectType(String typeName) {
        currentType = factory.getBuilderByName(typeName);
        if (currentType == null) {
            status("Тип не найден: " + typeName);
            tree = null;
            treeView.getItems().clear();
            treeCanvas.clear();
            return;
        }
        tree = new BinaryTree(currentType.getTypeComparator());
        treeView.getItems().clear();
        treeCanvas.clear();
        status("Выбран тип: " + currentType.typeName() + ". Создано пустое дерево.");
    }

    @FXML
    public void onAdd() {
        if (!ensureType()) return;
        String txt = inputField.getText();
        if (txt == null || txt.trim().isEmpty()) {
            statusError("Введите значение в поле ввода.");
            return;
        }
        try {
            Object val = currentType.parseValue(txt);
            tree.add(val);
            refreshView();
            inputField.clear();
            status("Элемент добавлен: " + val.toString());
        } catch (IllegalArgumentException ex) {
            statusError("Ошибка разбора значения: " + ex.getMessage());
        } catch (Exception ex) {
            statusError("Ошибка при добавлении: " + ex.getMessage());
        }
    }

    @FXML
    public void onRemove() {
        if (!ensureType()) return;
        int idx = treeView.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            statusError("Выберите элемент в списке для удаления.");
            return;
        }
        try {
            Object before = tree.get(idx);
            tree.remove(idx);
            refreshView();
            status("Удалён элемент (index " + idx + "): " + before);
        } catch (IndexOutOfBoundsException ex) {
            statusError("Индекс вне диапазона: " + ex.getMessage());
        } catch (Exception ex) {
            statusError("Ошибка при удалении: " + ex.getMessage());
        }
    }



    @FXML
    public void onBalance() {
        if (!ensureType()) return;
        tree.balance();
        refreshView();
        status("Дерево сбалансировано.");
    }

    @FXML
    public void onSave() {
        if (!ensureType()) return;
        FileChooser fc = new FileChooser();
        fc.setTitle("Сохранить дерево");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fc.showSaveDialog(getWindow());
        if (file == null) {
            status("Сохранение отменено.");
            return;
        }
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            // Сохраняем тип в первой строке, чтобы потом можно было восстановить тип
            w.write("#TYPE:" + currentType.typeName());
            w.newLine();
            List<Object> list = tree.toList();
            for (Object o : list) {
                w.write(o.toString());
                w.newLine();
            }
            status("Дерево сохранено в " + file.getName() + " (" + list.size() + " элементов).");
        } catch (IOException ex) {
            statusError("Ошибка при сохранении: " + ex.getMessage());
        }
    }

    @FXML
    public void onLoad() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Загрузить дерево");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fc.showOpenDialog(getWindow());
        if (file == null) {
            status("Загрузка отменена.");
            return;
        }
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String first = r.readLine();
            String fileTypeName = null;
            if (first != null && first.startsWith("#TYPE:")) {
                fileTypeName = first.substring("#TYPE:".length()).trim();
            } else {
                // если нет строки типа — вернемся в начало и попробуем загрузить текущим типом
                r.close();
                try (BufferedReader r2 = new BufferedReader(new FileReader(file))) {
                    loadValuesFromReader(r2, currentType);
                    return;
                }
            }

            UserType fileType = factory.getBuilderByName(fileTypeName);
            if (fileType == null) {
                // Неизвестный тип — спросим пользователя (alert) и отказ
                alertError("Файл содержит неизвестный тип: " + fileTypeName + ". Доступные типы: " + factory.getTypeNameList());
                statusError("Неизвестный тип в файле: " + fileTypeName);
                return;
            }

            // Если тип файла отличается от текущего — переключаем выбор
            if (!fileType.typeName().equals(currentType.typeName())) {
                typeComboBox.getSelectionModel().select(fileType.typeName());
                selectType(fileType.typeName());
            }

            // Теперь читаем оставшиеся строки и парсим
            BufferedReader rBody = new BufferedReader(new FileReader(file));
            // пропускаем header
            rBody.readLine();
            loadValuesFromReader(rBody, fileType);
        } catch (IOException ex) {
            statusError("Ошибка при загрузке: " + ex.getMessage());
        }
    }

    // Вспомогательный загрузчик из Reader
    private void loadValuesFromReader(BufferedReader reader, UserType type) {
        try {
            BinaryTree newTree = new BinaryTree(type.getTypeComparator());
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    Object val = type.parseValue(line.trim());
                    newTree.add(val);
                    count++;
                } catch (IllegalArgumentException ex) {
                    // Если одна строка не парсится — сообщим и пропустим
                    System.err.println("Skip line (can't parse): " + line + " -> " + ex.getMessage());
                }
            }
            this.tree = newTree;
            refreshView();
            status("Загружено " + count + " элементов типа " + type.typeName());
        } catch (IOException ex) {
            statusError("Ошибка чтения файла: " + ex.getMessage());
        }
    }

    // Обновить представление ListView и визуализатор дерева
    private void refreshView() {
        treeView.getItems().clear();
        if (tree == null) {
            treeCanvas.clear();
            return;
        }
        List<Object> list = tree.toList();
        for (Object o : list) treeView.getItems().add(o == null ? "null" : o.toString());
        // Обновление визуализатора
        treeCanvas.drawTree(tree);
    }

    // Убедиться, что выбран тип и дерево создано
    private boolean ensureType() {
        if (currentType == null) {
            statusError("Не выбран тип данных.");
            return false;
        }
        if (tree == null) {
            tree = new BinaryTree(currentType.getTypeComparator());
            status("Создано пустое дерево для типа " + currentType.typeName());
        }
        return true;
    }

    private Window getWindow() {
        return addBtn.getScene().getWindow();
    }

    private void status(String txt) {
        statusLabel.setText(txt);
    }

    private void statusError(String txt) {
        statusLabel.setText("Ошибка: " + txt);
    }

    private void alertError(String txt) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка");
        a.setHeaderText(null);
        a.setContentText(txt);
        a.showAndWait();
    }
}
