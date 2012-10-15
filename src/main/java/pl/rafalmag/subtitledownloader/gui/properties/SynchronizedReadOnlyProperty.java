package pl.rafalmag.subtitledownloader.gui.properties;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;

public class SynchronizedReadOnlyProperty<T> implements
		ReadOnlyProperty<T> {

	private final ReadOnlyProperty<T> property;

	public SynchronizedReadOnlyProperty(
			ReadOnlyProperty<T> property) {
		this.property = property;
	}

	@Override
	public synchronized void addListener(ChangeListener<? super T> arg0) {
		property.addListener(arg0);
	}

	@Override
	public synchronized void addListener(InvalidationListener arg0) {
		property.addListener(arg0);
	}

	@Override
	public synchronized Object getBean() {
		return property.getBean();
	}

	@Override
	public synchronized String getName() {
		return property.getName();
	}

	@Override
	public synchronized T getValue() {
		return property.getValue();
	}

	@Override
	public synchronized void removeListener(ChangeListener<? super T> arg0) {
		property.removeListener(arg0);
	}

	@Override
	public synchronized void removeListener(InvalidationListener arg0) {
		property.removeListener(arg0);
	}

}
