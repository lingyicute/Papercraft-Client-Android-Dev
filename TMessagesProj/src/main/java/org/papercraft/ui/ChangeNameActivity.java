/*
 * This is the source code of Papercraft for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.papercraft.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import org.papercraft.messenger.AndroidUtilities;
import org.papercraft.messenger.LocaleController;
import org.papercraft.tgnet.ConnectionsManager;
import org.papercraft.tgnet.TLRPC;
import org.papercraft.messenger.MessagesController;
import org.papercraft.messenger.NotificationCenter;
import org.papercraft.messenger.R;
import org.papercraft.messenger.UserConfig;
import org.papercraft.ui.ActionBar.ActionBar;
import org.papercraft.ui.ActionBar.ActionBarMenu;
import org.papercraft.ui.ActionBar.BaseFragment;
import org.papercraft.ui.ActionBar.Theme;
import org.papercraft.ui.ActionBar.ThemeDescription;
import org.papercraft.ui.Components.EditTextBoldCursor;
import org.papercraft.ui.Components.LayoutHelper;
import org.papercraft.ui.Components.OutlineTextContainerView;

import java.util.ArrayList;

public class ChangeNameActivity extends BaseFragment {

    private EditTextBoldCursor firstNameField;
    private OutlineTextContainerView firstNameFieldContainer;

    private EditTextBoldCursor lastNameField;
    private OutlineTextContainerView lastNameFieldContainer;

    private View headerLabelView;
    private View doneButton;

    private Theme.ResourcesProvider resourcesProvider;

    private final static int done_button = 1;

    public ChangeNameActivity(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
    }

    @Override
    public View createView(Context context) {
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue, resourcesProvider), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon, resourcesProvider), false);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("EditName", R.string.EditName));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == done_button) {
                    if (firstNameField.getText().length() != 0) {
                        saveName();
                        finishFragment();
                    }
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_ab_done, AndroidUtilities.dp(56), LocaleController.getString("Done", R.string.Done));

        TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(UserConfig.getInstance(currentAccount).getClientUserId());
        if (user == null) {
            user = UserConfig.getInstance(currentAccount).getCurrentUser();
        }

        LinearLayout linearLayout = new LinearLayout(context);
        fragmentView = linearLayout;
        fragmentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((LinearLayout) fragmentView).setOrientation(LinearLayout.VERTICAL);
        fragmentView.setOnTouchListener((v, event) -> true);

        firstNameFieldContainer = new OutlineTextContainerView(context);
        firstNameFieldContainer.setText(LocaleController.getString("FirstName", R.string.FirstName));
        linearLayout.addView(firstNameFieldContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 24, 24, 24, 0));

        firstNameField = new EditTextBoldCursor(context) {
            @Override
            protected Theme.ResourcesProvider getResourcesProvider() {
                return resourcesProvider;
            }
        };
        firstNameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        firstNameField.setBackground(null);
        firstNameField.setSingleLine(true);
        firstNameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        firstNameField.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourcesProvider));
        firstNameField.setCursorWidth(1.5f);
        firstNameField.setCursorSize(AndroidUtilities.dp(20));
        firstNameField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        firstNameField.setOnFocusChangeListener((v, hasFocus) -> firstNameFieldContainer.animateSelection(hasFocus ? 1 : 0));
        int padding = AndroidUtilities.dp(16);
        firstNameField.setPadding(padding, padding, padding, padding);
        firstNameFieldContainer.addView(firstNameField, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        firstNameFieldContainer.attachEditText(firstNameField);
        firstNameField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_ACTION_DONE) {
                doneButton.performClick();
                return true;
            }
            return false;
        });

        lastNameFieldContainer = new OutlineTextContainerView(context);
        lastNameFieldContainer.setText(LocaleController.getString("LastName", R.string.LastName));
        linearLayout.addView(lastNameFieldContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 24, 24, 24, 0));

        lastNameField = new EditTextBoldCursor(context) {
            @Override
            protected Theme.ResourcesProvider getResourcesProvider() {
                return resourcesProvider;
            }
        };
        lastNameField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        lastNameField.setBackground(null);
        lastNameField.setSingleLine(true);
        lastNameField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        lastNameField.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourcesProvider));
        lastNameField.setCursorWidth(1.5f);
        lastNameField.setCursorSize(AndroidUtilities.dp(20));
        lastNameField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        lastNameField.setOnFocusChangeListener((v, hasFocus) -> lastNameFieldContainer.animateSelection(hasFocus ? 1 : 0));
        lastNameField.setPadding(padding, padding, padding, padding);
        lastNameFieldContainer.addView(lastNameField, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        lastNameFieldContainer.attachEditText(lastNameField);
        lastNameField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                doneButton.performClick();
                return true;
            }
            return false;
        });

        if (user != null) {
            firstNameField.setText(user.first_name);
            firstNameField.setSelection(firstNameField.length());
            lastNameField.setText(user.last_name);
        }

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean animations = preferences.getBoolean("view_animations", true);
        if (!animations) {
            firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(firstNameField);
        }
    }

    private void saveName() {
        TLRPC.User currentUser = UserConfig.getInstance(currentAccount).getCurrentUser();
        if (currentUser == null || lastNameField.getText() == null || firstNameField.getText() == null) {
            return;
        }
        String newFirst = firstNameField.getText().toString();
        String newLast = lastNameField.getText().toString();
        if (currentUser.first_name != null && currentUser.first_name.equals(newFirst) && currentUser.last_name != null && currentUser.last_name.equals(newLast)) {
            return;
        }
        TLRPC.TL_account_updateProfile req = new TLRPC.TL_account_updateProfile();
        req.flags = 3;
        currentUser.first_name = req.first_name = newFirst;
        currentUser.last_name = req.last_name = newLast;
        TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(UserConfig.getInstance(currentAccount).getClientUserId());
        if (user != null) {
            user.first_name = req.first_name;
            user.last_name = req.last_name;
        }
        UserConfig.getInstance(currentAccount).saveConfig(true);
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_NAME);
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> {

        });
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return resourcesProvider;
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(() -> {
                if (firstNameField != null) {
                    firstNameField.requestFocus();
                    AndroidUtilities.showKeyboard(firstNameField);
                }
            }, 100);
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));

        return themeDescriptions;
    }
}
