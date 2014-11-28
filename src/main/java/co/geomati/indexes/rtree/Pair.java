package co.geomati.indexes.rtree;

public class Pair<T, S> {

	public Pair(T f, S s) {
		first = f;
		second = s;
	}

	public T getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "(" + first.toString() + ", " + second.toString() + ")";
	}

	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) obj;
			return first.equals(pair.first) && second.equals(pair.second);
		} else {
			return false;
		}
	}

	private final T first;
	private final S second;
}
