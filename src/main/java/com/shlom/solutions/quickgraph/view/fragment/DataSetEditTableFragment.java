package com.shlom.solutions.quickgraph.view.fragment;

import android.net.Uri;

import com.shlom.solutions.quickgraph.model.database.dbmodel.CoordinateModel;
import com.shlom.solutions.quickgraph.view.fragment.dialog.imp.ImportDialogFragment;

import java.util.List;

public class DataSetEditTableFragment extends BaseDataSetEditFragment
        implements ImportDialogFragment.OnReceivedImportResult {

//    private static final String TAG_IMPORT_DIALOG = "import_dialog";
//
//    @Override
//    protected void onCreateView(View rootView, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(rootView, savedInstanceState);
//
//        getStandaloneDataSet().setType(DataSetModel.Type.FROM_TABLE);
//        addAdapterImpl(new Delegate() {
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
//                return new CoordinateItemVH(LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.list_item_edit_table_coordinate, parent, false));
//            }
//
//            @Override
//            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//                CoordinateItemVH holder = (CoordinateItemVH) viewHolder;
//                CoordinateModel coordinate = getStandaloneDataSet().getCoordinates().get(position - 1);
//                if (holder.xInput.getEditText() != null) {
//                    holder.xInput.getEditText().setText(String.valueOf(coordinate.getX()));
//                }
//                if (holder.yInput.getEditText() != null) {
//                    holder.yInput.getEditText().setText(String.valueOf(coordinate.getY()));
//                }
//            }
//
//            @Override
//            public boolean isCurrent(int position) {
//                return position > 0 && getAdapter().getItemCount() - 1 != position;
//            }
//
//            @Override
//            public int getItemCount() {
//                if (getStandaloneDataSet().getCoordinates() == null) return 0;
//                return getStandaloneDataSet().getCoordinates().size();
//            }
//        });
//
//        addAdapterImpl(new Delegate() {
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
//                return new CoordinateItemAddVH(LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.list_item_edit_table_coordinate_add, parent, false));
//            }
//
//            @Override
//            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//            }
//
//            @Override
//            public boolean isCurrent(int position) {
//                return getAdapter().getItemCount() - 1 == position;
//            }
//
//            @Override
//            public int getItemCount() {
//                return 1;
//            }
//        });
//    }
//
//    @Override
//    protected void onConfirmationSaving() {
//        super.onConfirmationSaving();
//
//        long uid = getDataBaseManager().generateUID(CoordinateModel.class);
//        for (CoordinateModel coordinate : getStandaloneDataSet().getCoordinates()) {
//            if (coordinate.getUid() == 0) {
//                coordinate.setUid(uid);
//                uid++;
//            }
//        }
//    }
//
    @Override
    public void onReceivedImportResult(Uri uri, List<CoordinateModel> result) {
//        getStandaloneDataSet().setCoordinates(new RealmList<>(result.toArray(new CoordinateModel[result.size()])));
//        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
//        if (cursor != null && cursor.moveToNext()) {
//            try {
//                getStandaloneDataSet().setDescription(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
//            } finally {
//                cursor.close();
//            }
//        } else {
//            getStandaloneDataSet().setDescription(new File(uri.getPath()).getName());
//        }
//        getAdapter().notifyDataSetChanged();
    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        inflater.inflate(R.menu.import_menu, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_import:
//                new ImportDialogFragment().show(getChildFragmentManager(), TAG_IMPORT_DIALOG);
//                break;
//            case R.id.action_clear_all:
//                getStandaloneDataSet().getCoordinates().clear();
//                getAdapter().notifyDataSetChanged();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private class CoordinateItemVH extends RecyclerView.ViewHolder {
//
//        private ImageView deleteButton;
//        private TextInputLayout xInput;
//        private TextInputLayout yInput;
//
//        public CoordinateItemVH(View itemView) {
//            super(itemView);
//
//            deleteButton = (ImageView) itemView.findViewById(R.id.edit_data_set_table_coordinate_delete);
//            deleteButton.setOnClickListener(view -> {
//                if (getAdapterPosition() != -1) {
//                    getStandaloneDataSet().getCoordinates().remove(getAdapterPosition() - 1);
//                    getAdapter().notifyItemRemoved(getAdapterPosition());
//                }
//            });
//            xInput = (TextInputLayout) itemView.findViewById(R.id.edit_data_set_table_coordinate_x);
//            yInput = (TextInputLayout) itemView.findViewById(R.id.edit_data_set_table_coordinate_y);
//            if (xInput.getEditText() != null) {
//                setFocusController(xInput.getEditText());
//                xInput.getEditText().addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        super.onTextChanged(s, start, before, count);
//                        float x = s.length() == 0 ? 0 : Float.valueOf(s.toString());
//                        getStandaloneDataSet().getCoordinates().get(getAdapterPosition() - 1).setX(x);
//                    }
//                });
//            }
//            if (yInput.getEditText() != null) {
//                setFocusController(yInput.getEditText());
//                yInput.getEditText().addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        super.onTextChanged(s, start, before, count);
//                        float y = s.length() == 0 ? 0 : Float.valueOf(s.toString());
//                        getStandaloneDataSet().getCoordinates().get(getAdapterPosition() - 1).setY(y);
//                    }
//                });
//            }
//        }
//    }
//
//    private class CoordinateItemAddVH extends RecyclerView.ViewHolder {
//
//        private Button addButton;
//
//        public CoordinateItemAddVH(View itemView) {
//            super(itemView);
//
//            addButton = (Button) itemView.findViewById(R.id.edit_data_set_table_coordinate_add);
//            addButton.setOnClickListener(v -> {
//                if (getAdapterPosition() != -1) {
//                    getStandaloneDataSet().getCoordinates().add(new CoordinateModel());
//                    getAdapter().notifyItemInserted(getAdapterPosition());
//                }
//            });
//        }
//    }
}
