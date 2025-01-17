package org.papercraft.ui;

import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.papercraft.messenger.AndroidUtilities;
import org.papercraft.messenger.DocumentObject;
import org.papercraft.messenger.ImageLocation;
import org.papercraft.messenger.LocaleController;
import org.papercraft.messenger.MediaDataController;
import org.papercraft.messenger.R;
import org.papercraft.messenger.SvgHelper;
import org.papercraft.messenger.UserConfig;
import org.papercraft.tgnet.TLRPC;
import org.papercraft.ui.ActionBar.Theme;
import org.papercraft.ui.Components.BackupImageView;
import org.papercraft.ui.Components.LayoutHelper;

public class CreateTopicEmptyView extends LinearLayout {

    private final Theme.ResourcesProvider resourcesProvider;
    BackupImageView backupImageView;


    public CreateTopicEmptyView(Context context, FrameLayout parent, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;

        setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(18), this, parent, getThemedPaint(Theme.key_paint_chatActionBackground)));
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(12));
        setOrientation(LinearLayout.VERTICAL);

        backupImageView = new BackupImageView(context);

        TextView headerTextView = new TextView(context);
        headerTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        headerTextView.setTextColor(getThemedColor(Theme.key_chat_serviceText));
        headerTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        headerTextView.setMaxWidth(AndroidUtilities.dp(210));

        headerTextView.setText(LocaleController.getString(R.string.AlmostDone));


        TextView description = new TextView(context);
        description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        description.setTextColor(getThemedColor(Theme.key_chat_serviceText));
        description.setGravity(Gravity.CENTER_HORIZONTAL);
        description.setMaxWidth(AndroidUtilities.dp(160));

        description.setText(LocaleController.getString(R.string.TopicEmptyViewDescription));

        addView(backupImageView, LayoutHelper.createLinear(58, 58, Gravity.CENTER_HORIZONTAL, 0, 8, 0, 8));
        addView(headerTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 0, 2, 0));
        addView(description, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));

        //TODO topics optimize
        setSticker();
    }


    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    private void setSticker() {
        String imageFilter = null;
        TLRPC.Document document = null;
        TLRPC.TL_messages_stickerSet set = null;
        document = MediaDataController.getInstance(UserConfig.selectedAccount).getEmojiAnimatedSticker("\uD83E\uDD73");

        if (document != null) {
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document.thumbs,  Theme.key_emptyListPlaceholder, 0.2f);
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }

            ImageLocation imageLocation = ImageLocation.getForDocument(document);
            backupImageView.setImage(imageLocation, imageFilter, "tgs", svgThumb, set);
        }
    }
}
