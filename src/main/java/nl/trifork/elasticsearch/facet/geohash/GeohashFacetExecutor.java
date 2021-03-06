package nl.trifork.elasticsearch.facet.geohash;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.elasticsearch.index.fielddata.GeoPointValues;
import org.elasticsearch.index.fielddata.IndexGeoPointFieldData;
import org.elasticsearch.search.facet.FacetExecutor;
import org.elasticsearch.search.facet.InternalFacet;

public class GeohashFacetExecutor extends FacetExecutor {

	private final IndexGeoPointFieldData<?> indexFieldData;
	private final double factor;
    private final boolean showGeohashCell;
	private final ClusterBuilder builder;

	public GeohashFacetExecutor(IndexGeoPointFieldData<?> indexFieldData, double factor, boolean showGeohashCell) {
		this.indexFieldData = indexFieldData;
		this.factor = factor;
        this.showGeohashCell = showGeohashCell;
		this.builder = new ClusterBuilder(factor);
	}

	@Override
	public FacetExecutor.Collector collector() {
		return new Collector();
	}

	@Override
	public InternalFacet buildFacet(String facetName) {
		return new InternalGeohashFacet(facetName, factor, showGeohashCell, builder.build());
	}

	private class Collector extends FacetExecutor.Collector {

		private GeoPointValues values;

		@Override
		public void setNextReader(AtomicReaderContext context) throws IOException {
			values = indexFieldData.load(context).getGeoPointValues();
		}

		@Override
		public void collect(int docId) throws IOException {
            final int length = values.setDocument(docId);
            for (int i = 0; i < length; i++) {
                builder.add(GeoPoints.copy(values.nextValue())); // nextValue() may recycle GeoPoint instances!
            }
		}

		@Override
		public void postCollection() {

		}
	}
}
