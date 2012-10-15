package pl.rafalmag.subtitledownloader.gui.properties;

import javafx.beans.value.WritableValue;

public class SynchronizedWritableValue<T> implements WritableValue<T> {

	private final WritableValue<T> writableValue;

	public SynchronizedWritableValue(WritableValue<T> writableValue) {
		this.writableValue = writableValue;
	}

	@Override
	public synchronized T getValue() {
		return writableValue.getValue();
	}

	@Override
	public synchronized void setValue(T value) {
		writableValue.setValue(value);
	}

}
