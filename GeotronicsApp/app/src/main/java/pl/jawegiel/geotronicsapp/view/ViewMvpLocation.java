package pl.jawegiel.mvpappwithlocation.view;

public interface ViewMvpLocation {

    void configureProgressDialog();
    void showProgressDialog();
    void dismissProgressDialogWithSuccess(String response);
    void dismissProgressDialogWithError(String errorMessage);
}
