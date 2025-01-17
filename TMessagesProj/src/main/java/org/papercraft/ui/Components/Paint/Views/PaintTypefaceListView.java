package org.papercraft.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.papercraft.messenger.AndroidUtilities;
import org.papercraft.ui.Components.Paint.PaintTypeface;
import org.papercraft.ui.Components.RecyclerListView;

public class PaintTypefaceListView extends RecyclerListView {
    private Path mask = new Path();
    private Consumer<Path> maskProvider;

    public PaintTypefaceListView(Context context) {
        super(context);

        setWillNotDraw(false);
        setLayoutManager(new LinearLayoutManager(context));
        setAdapter(new RecyclerListView.SelectionAdapter() {

            @Override
            public boolean isEnabled(ViewHolder holder) {
                return true;
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = new PaintTextOptionsView.TypefaceCell(parent.getContext());
                v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new Holder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                PaintTextOptionsView.TypefaceCell cell = (PaintTextOptionsView.TypefaceCell) holder.itemView;
                cell.bind(PaintTypeface.get().get(position));
            }

            @Override
            public int getItemCount() {
                return PaintTypeface.get().size();
            }
        });

        setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(Math.min(PaintTypeface.get().size(), 6) * AndroidUtilities.dp(48) + AndroidUtilities.dp(16), MeasureSpec.EXACTLY));
    }

    @Override
    public void draw(Canvas c) {
        if (maskProvider != null) {
            maskProvider.accept(mask);

            c.save();
            c.clipPath(mask);
        }
        super.draw(c);
        if (maskProvider != null) {
            c.restore();
        }
    }

    public void setMaskProvider(Consumer<Path> maskProvider) {
        this.maskProvider = maskProvider;
        invalidate();
    }
}
