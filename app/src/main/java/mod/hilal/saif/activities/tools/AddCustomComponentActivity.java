package mod.hilal.saif.activities.tools;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.sketchware.remod.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import a.a.a.aB;
import a.a.a.wq;
import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.elfilibustero.sketch.lib.ui.SketchFilePickerDialog;
import mod.hasrat.tools.ComponentHelper;
import mod.hey.studios.util.Helper;
import mod.hilal.saif.components.ComponentsHandler;
import mod.jbk.util.OldResourceIdMapper;

public class AddCustomComponentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText componentName;
    private EditText componentId;
    private EditText componentIcon;
    private EditText componentVarTypeName;
    private EditText componentTypeName;
    private EditText componentBuildClass;
    private EditText componentTypeClass;
    private EditText componentDesc;
    private EditText componentDocUrl;
    private EditText componentAddVar;
    private EditText componentDefineAddVar;
    private EditText componentImports;

    private boolean isEditMode = false;
    private int position = 0;

    private final String path = wq.getCustomComponent();

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.manage_custom_component_add);
        init();
    }

    private void init() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        if (getIntent().hasExtra("pos")) {
            isEditMode = true;
            position = getIntent().getIntExtra("pos", 0);
        }
        getViewsById();
        if (isEditMode) {
            setTitle(Helper.getResString(R.string.event_title_edit_component));
            fillUp();
        } else {
            setTitle(Helper.getResString(R.string.event_title_add_new_component));
            initializeHelper();
        }
    }

    private void fillUp() {
        if (FileUtil.isExistFile(path)) {
            ArrayList<HashMap<String, Object>> list = new Gson().fromJson(FileUtil.readFile(path), Helper.TYPE_MAP_LIST);
            HashMap<String, Object> map = list.get(position);
            setupViews(map);
        }
    }

    private void getViewsById() {
        componentName = findViewById(R.id.component_name);
        componentId = findViewById(R.id.component_id);
        componentIcon = findViewById(R.id.component_icon);
        componentVarTypeName = findViewById(R.id.component_var_type_name);
        componentTypeName = findViewById(R.id.component_type_name);
        componentBuildClass = findViewById(R.id.component_build_class);
        componentTypeClass = findViewById(R.id.component_type_class);
        componentDesc = findViewById(R.id.component_description);
        componentDocUrl = findViewById(R.id.component_doc_url);
        componentAddVar = findViewById(R.id.component_add_var);
        componentDefineAddVar = findViewById(R.id.component_def_add_var);
        componentImports = findViewById(R.id.component_imports);
        findViewById(R.id.btn_import).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.pick).setOnClickListener(this);
    }

    private void initializeHelper() {
        componentName.addTextChangedListener(new ComponentHelper(new EditText[]{componentBuildClass, componentVarTypeName, componentTypeName, componentTypeClass}, componentTypeClass));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_save) {
            if (!isImportantFieldsEmpty()) {
                if (OldResourceIdMapper.isValidIconId(componentIcon.getText().toString())) {
                    save();
                } else {
                    SketchwareUtil.toastError(Helper.getResString(R.string.invalid_icon_id));
                    componentIcon.requestFocus();
                }
            } else {
                SketchwareUtil.toastError(Helper.getResString(R.string.invalid_required_fields));
            }
        } else if (id == R.id.btn_import) {
            showFilePickerDialog();
        } else if (id == R.id.pick) {
            showIconSelectorDialog();
        }
    }

    private void setupViews(HashMap<String, Object> map) {
        componentName.setText((String) map.get("name"));
        componentId.setText((String) map.get("id"));
        componentIcon.setText((String) map.get("icon"));
        componentVarTypeName.setText((String) map.get("varName"));
        componentTypeName.setText((String) map.get("typeName"));
        componentBuildClass.setText((String) map.get("class"));
        componentTypeClass.setText((String) map.get("buildClass"));
        componentDesc.setText((String) map.get("description"));
        componentDocUrl.setText((String) map.get("url"));
        componentAddVar.setText((String) map.get("additionalVar"));
        componentDefineAddVar.setText((String) map.get("defineAdditionalVar"));
        componentImports.setText((String) map.get("imports"));
    }

    private void showIconSelectorDialog() {
        new IconSelectorDialog(this, componentIcon).show();
    }

    private boolean isImportantFieldsEmpty() {
        return componentName.getText().toString().isEmpty()
                || componentId.getText().toString().isEmpty()
                || componentIcon.getText().toString().isEmpty()
                || componentTypeName.getText().toString().isEmpty()
                || componentVarTypeName.getText().toString().isEmpty()
                || componentTypeClass.getText().toString().isEmpty()
                || componentBuildClass.getText().toString().isEmpty();
    }

    private void save() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        if (FileUtil.isExistFile(path)) {
            list = new Gson().fromJson(FileUtil.readFile(path), Helper.TYPE_MAP_LIST);
        }
        HashMap<String, Object> map = new HashMap<>();
        if (isEditMode) {
            map = list.get(position);
        }
        map.put("name", componentName.getText().toString());
        map.put("id", componentId.getText().toString());
        map.put("icon", componentIcon.getText().toString());
        map.put("varName", componentVarTypeName.getText().toString());
        map.put("typeName", componentTypeName.getText().toString());
        map.put("buildClass", componentBuildClass.getText().toString());
        map.put("class", componentTypeClass.getText().toString());
        map.put("description", componentDesc.getText().toString());
        map.put("url", componentDocUrl.getText().toString());
        map.put("additionalVar", componentAddVar.getText().toString());
        map.put("defineAdditionalVar", componentDefineAddVar.getText().toString());
        map.put("imports", componentImports.getText().toString());
        if (!isEditMode) {
            list.add(map);
        }
        FileUtil.writeFile(path, new Gson().toJson(list));
        SketchwareUtil.toast(Helper.getResString(R.string.common_word_saved));
        finish();
    }

    private void showFilePickerDialog() {
        SketchFilePickerDialog dialog = new SketchFilePickerDialog(this)
                .allowExtension("json")
                .setFilePath(FileUtil.getExternalStorageDir())
                .setOnFileSelectedListener((SketchFilePickerDialog _dialog, File file) -> {
                    try {
                        selectComponentToImport(file.getAbsolutePath());
                    } catch (Exception e) {
                        SketchwareUtil.toastError(Helper.getResString(R.string.publish_message_dialog_invalid_json));
                    }
                    _dialog.dismiss();
                });
        dialog.setTitle(Helper.getResString(R.string.common_word_import));
        dialog.setIcon(R.drawable.file_48_blue);
        dialog.show();
    }

    private void selectComponentToImport(String path) {
        var readResult = ComponentsHandler.readComponents(path);
        if (readResult.first.isPresent()) {
            SketchwareUtil.toastError(readResult.first.get());
            return;
        }
        var components = readResult.second;

        var componentNames = components.stream()
                .map(component -> (String) component.get("name"))
                .collect(Collectors.toList());
        if (componentNames.size() > 1) {
            var dialog = new aB(this);
            dialog.b(Helper.getResString(R.string.logic_editor_title_select_component));
            var choiceToImport = new AtomicInteger(-1);
            var listView = new ListView(this);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, componentNames);
            listView.setAdapter(arrayAdapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setDivider(null);
            listView.setDividerHeight(0);
            listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                choiceToImport.set(position);
            });
            dialog.a(listView);
            dialog.b(Helper.getResString(R.string.common_word_import), view -> {
                int position = choiceToImport.get();
                var component = components.get(position);
                if (position != -1 && ComponentsHandler.isValidComponent(component)) {
                    setupViews(component);
                } else {
                    SketchwareUtil.toastError(Helper.getResString(R.string.invalid_component));
                }
                dialog.dismiss();
            });
            dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
            dialog.show();
        } else {
            var component = components.get(0);
            if (ComponentsHandler.isValidComponent(component)) {
                setupViews(component);
            } else {
                SketchwareUtil.toastError(Helper.getResString(R.string.invalid_component));
            }
        }
    }
}
