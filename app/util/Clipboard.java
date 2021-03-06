package util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.DomainModel;
import models.Series;
import models.Study;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import play.db.jpa.GenericModel;

public class Clipboard {

	static final String SEPARATOR = ",";

	private Set<Item> items = new HashSet<Item>();

	public Clipboard(String clipboard) {
		if (clipboard != null && !clipboard.isEmpty()) {
			for (String item : clipboard.split(SEPARATOR)) {
				items.add(new Item(item));
			}
		}
	}

	public void add(String type, long pk) throws ClassNotFoundException {
		items.add(new Item(type, pk));
	}

	public void remove(String type, long pk) throws ClassNotFoundException {
		items.remove(new Item(type, pk));
	}

	public void clear() {
		items.clear();
	}

	public Set<GenericModel> getObjects() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Set<GenericModel> objects = new HashSet<GenericModel>();
		for (Item item : items) {
			GenericModel object = item.getModel();
			if (!(object instanceof Series) || !objects.contains(((Series) object).study)) {
				objects.add(object);
			}
		}
		return objects;
	}

	@Override
	public String toString() {
		return StringUtils.join(items, SEPARATOR);
	}

	public static class Item {

		private static final String DELIMITER = ":";
		private static final List types = Arrays.asList(Study.class, Series.class);

		public Class type;
		public long pk;

		public Item(String item) {
			String[] parts = item.split(DELIMITER);
			type = (Class) types.get(Integer.parseInt(parts[0]));
			pk = Long.parseLong(parts[1]);
		}

		private Item(Class type, long pk) {
			this.type = type;
			this.pk = pk;
		}

		public Item(String type, long pk) throws ClassNotFoundException {
			this(Class.forName(String.format("models.%s", type)), pk);
		}

		public Item(DomainModel object) {
			this(object.getClass(), object.pk);
		}

		public static Item[] serialize(List<DomainModel> objects) {
			Item[] items = new Item[objects.size()];
			for (int i = 0; i < objects.size(); i++) {
				DomainModel object = objects.get(i);
				items[i] = new Item(object);
			}
			return items;
		}

		public DomainModel getModel() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			return (DomainModel) type.getMethod("findById", Object.class).invoke(null, pk);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}

		@Override
		public String toString() {
			return String.format("%s%s%s", types.indexOf(type), DELIMITER, pk);
		}
	}

}
