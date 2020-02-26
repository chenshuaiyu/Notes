# RecyclerView源码-观察者模式

向RecyclerView添加数据时，会调用Adapter.notifyXXX()，这就是对观察者模式的一种应用。

```java
public abstract static class Adapter<VH extends ViewHolder> {
    private final AdapterDataObservable mObservable = new AdapterDataObservable();
    
    public final void notifyDataSetChanged() {
        mObservable.notifyChanged();
    }
}

static class AdapterDataObservable extends Observable<AdapterDataObserver> {
    
    public void notifyChanged() {
        for (int i = mObservers.size() - 1; i >= 0; i--) {
            mObservers.get(i).onChanged();
        }
    }
    //...
}

public abstract static class AdapterDataObserver {
    public void onChanged() {
    }

    public void onItemRangeChanged(int positionStart, int itemCount) {
    }

    public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        onItemRangeChanged(positionStart, itemCount);
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
    }

    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
    }
}

public abstract class Observable<T> {
    protected final ArrayList<T> mObservers = new ArrayList<T>();

    public void registerObserver(T observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        synchronized(mObservers) {
            if (mObservers.contains(observer)) {
                throw new IllegalStateException("Observer " + observer + " is already registered.");
            }
            mObservers.add(observer);
        }
    }

    public void unregisterObserver(T observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The observer is null.");
        }
        synchronized(mObservers) {
            int index = mObservers.indexOf(observer);
            if (index == -1) {
                throw new IllegalStateException("Observer " + observer + " was not registered.");
            }
            mObservers.remove(index);
        }
    }

    public void unregisterAll() {
        synchronized(mObservers) {
            mObservers.clear();
        }
    }
}
```



```java
public void setAdapter(@Nullable Adapter adapter) {
    // bail out if layout is frozen
    setLayoutFrozen(false);
    setAdapterInternal(adapter, false, true);
    processDataSetCompletelyChanged(false);
    requestLayout();
}

private void setAdapterInternal(@Nullable Adapter adapter, boolean compatibleWithPrevious,
                                boolean removeAndRecycleViews) {
    if (mAdapter != null) {
        mAdapter.unregisterAdapterDataObserver(mObserver);
        mAdapter.onDetachedFromRecyclerView(this);
    }
    if (!compatibleWithPrevious || removeAndRecycleViews) {
        removeAndRecycleViews();
    }
    mAdapterHelper.reset();
    final Adapter oldAdapter = mAdapter;
    mAdapter = adapter;
    if (adapter != null) {
        adapter.registerAdapterDataObserver(mObserver);
        adapter.onAttachedToRecyclerView(this);
    }
    if (mLayout != null) {
        mLayout.onAdapterChanged(oldAdapter, mAdapter);
    }
    mRecycler.onAdapterChanged(oldAdapter, mAdapter, compatibleWithPrevious);
    mState.mStructureChanged = true;
}
```

```java
public abstract static class Adapter<VH extends ViewHolder> {

     public void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
        mObservable.registerObserver(observer);
     }
     
    public void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
        mObservable.unregisterObserver(observer);
    }
    
    //...
}
```

