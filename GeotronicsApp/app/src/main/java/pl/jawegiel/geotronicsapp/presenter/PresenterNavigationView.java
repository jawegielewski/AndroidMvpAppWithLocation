package pl.jawegiel.mvpappwithlocation.presenter;

import pl.jawegiel.mvpappwithlocation.view.ViewMvpNavigationView;

public class PresenterNavigationView {

    ViewMvpNavigationView viewMvpNavigationView;

    public PresenterNavigationView(ViewMvpNavigationView viewMvpNavigationView) {
        this.viewMvpNavigationView = viewMvpNavigationView;
    }

    public void setMapItem() {
        viewMvpNavigationView.setMapItem();
    }

    public void setExitItem() {
        viewMvpNavigationView.setExitItem();
    }
}
