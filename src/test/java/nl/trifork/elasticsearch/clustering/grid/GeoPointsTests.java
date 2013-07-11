package nl.trifork.elasticsearch.clustering.grid;

import static nl.trifork.elasticsearch.clustering.grid.test.Places.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import nl.trifork.elasticsearch.facet.geohash.GeoPoints;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.unit.DistanceUnit;
import org.testng.annotations.Test;

public class GeoPointsTests {

	@Test
	public void testReadFromWriteTo() throws IOException {
		BytesStreamOutput out = new BytesStreamOutput();
		GeoPoints.writeTo(LAS_VEGAS, out);
		BytesStreamInput in = new BytesStreamInput(out.bytes());
		GeoPoint point = GeoPoints.readFrom(in);
		assertThat("Latitude", point.lat(), equalTo(LAS_VEGAS.lat()));
		assertThat("Longitude", point.lon(), equalTo(LAS_VEGAS.lon()));
	}

	@Test
	public void testDistance() throws IOException {
		assertThat("Distance (mi)", GeoPoints.distance(LAS_VEGAS, SAN_DIEGO, DistanceUnit.MILES), closeTo(250.0, 5.0));
	}
}
