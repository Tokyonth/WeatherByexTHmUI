package com.tokyonth.weather.widget.search;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.SearchCityAdapter;
import com.tokyonth.weather.model.CitySelectionModel;
import com.tokyonth.weather.model.bean.City;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends DialogFragment implements
        DialogInterface.OnKeyListener, ViewTreeObserver.OnPreDrawListener,
        IOnItemClickListener, SearchAnim.AnimListener, View.OnClickListener {

    public static final String TAG = "SearchFragment";
    private ImageView ivSearchClean;
    private EditText etSearchKeyword;
    private RecyclerView rvSearchCity;

    private View rootView;
    private SearchAnim mCircularRevealAnim;
    private List<City> foundCityList = new ArrayList<>();

    private CitySelectionModel selectionModel;
    private SearchCityAdapter searchCityAdapter;
    private Context context;

    public void setCitySelect(CitySelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.SearchDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_search_fragment, container, false);
        context = getContext();
        initView();
        return rootView;
    }

    private void initView() {
        ImageView ivSearchBack = rootView.findViewById(R.id.iv_search_back);
        etSearchKeyword = rootView.findViewById(R.id.et_search_keyword);
        rvSearchCity = rootView.findViewById(R.id.rv_search_history);
        ivSearchClean = rootView.findViewById(R.id.iv_search_clean);
        View viewSearchOutside = rootView.findViewById(R.id.view_search_outside);

        //实例化动画效果
        mCircularRevealAnim = new SearchAnim();
        //监听动画
        mCircularRevealAnim.setAnimListener(this);

        if (getDialog() != null)
        getDialog().setOnKeyListener(this);//键盘按键监听
        ivSearchClean.getViewTreeObserver().addOnPreDrawListener(this);//绘制监听

        //初始化recyclerView
        rvSearchCity.setLayoutManager(new LinearLayoutManager(getContext()));
        searchCityAdapter = new SearchCityAdapter(foundCityList);
        rvSearchCity.setAdapter(searchCityAdapter);

        //监听编辑框文字改变
        etSearchKeyword.addTextChangedListener(new TextWatcherImpl());
        //使输入框获得焦点
        etSearchKeyword.requestFocus();
        //监听点击
        ivSearchBack.setOnClickListener(this);
        viewSearchOutside.setOnClickListener(this);
        ivSearchClean.setOnClickListener(this);
    }

    public void showFragment(FragmentManager fragmentManager, String tag) {
        if (!this.isAdded()) {
            this.show(fragmentManager, tag);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_search_back || view.getId() == R.id.view_search_outside) {
            hideAnim();
        } else if (view.getId() == R.id.iv_search_clean) {
            etSearchKeyword.setText("");
        }
    }

    private void initDialog() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 0.98); //DialogSearch的宽
        assert window != null;
        window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.DialogEmptyAnimation);//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            hideAnim();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
          if (foundCityList.size() == 0) {
              Toast.makeText(context, "无该城市信息", Toast.LENGTH_SHORT).show();
          } else {
              Toast.makeText(context, "需要选择一个城市", Toast.LENGTH_SHORT).show();
          }
        }
        return false;
    }

    /**
     * 监听搜索键绘制时
     */
    @Override
    public boolean onPreDraw() {
        ivSearchClean.getViewTreeObserver().removeOnPreDrawListener(this);
        mCircularRevealAnim.show(rootView);
        return true;
    }

    /**
     * 搜索框动画隐藏完毕时调用
     */
    @Override
    public void onHideAnimationEnd() {
        etSearchKeyword.setText("");
        dismiss();
    }

    /**
     * 搜索框动画显示完毕时调用
     */
    @Override
    public void onShowAnimationEnd() {
        if (isVisible()) {
            openKeyboard(context, etSearchKeyword);
        }
    }

    @Override
    public void onItemClick(City city) {
        //Toast.makeText(getContext(), city.getCityName(),Toast.LENGTH_SHORT).show();
        closeKeyboard(context, etSearchKeyword);
        selectionModel.isCity(city);
    }

    private class TextWatcherImpl implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String str = charSequence.toString().trim();
            String queriedCity = str + "%";
            foundCityList = LitePal.where("cityname like ?",queriedCity).find(City.class);
            searchCityAdapter = new SearchCityAdapter(foundCityList);
            rvSearchCity.setAdapter(searchCityAdapter);
            searchCityAdapter.notifyDataSetChanged();
            searchCityAdapter.setOnItemClickListener(SearchFragment.this);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String keyword = editable.toString();
            if (TextUtils.isEmpty(keyword.trim())) {
                foundCityList.clear();
                searchCityAdapter.notifyDataSetChanged();
            }
        }
    }

    private void hideAnim() {
        closeKeyboard(context, etSearchKeyword);
        mCircularRevealAnim.hide(rootView);
    }

    private void openKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void closeKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

}
