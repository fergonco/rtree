/*
 * GearScape is a Geographic Information System focused on geo-processing. 
 * It is able to retrieve, process and display spatial data of both vector 
 * and raster type. GearScape is distributed under GPL 3 license.
 *
 * Copyright (C) 2009 the GearScape team
 *
 * This file is part of the GearScape's project source code.
 *
 * GearScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GearScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GearScape. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://gearscape.forge.osor.eu/>
 */
/*
 * GELAT is a Geographic information system focused in geoprocessing.
 * It's able to manipulate and create vector and raster spatial information. GELAT
 * is distributed under GPL 3 license.
 * 
 * Copyright (C) 2009 Fernando GONZALEZ CORTES, Victor GONZALEZ CORTES
 * 
 * This file is part of GELAT.
 * 
 * GELAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GELAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GELAT. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult:
 *    <http://gelat.forge.osor.eu/>
 * 
 * or contact directly fergonco _at_ gmail.com
 */

/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package co.geomati.rtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;
import co.geomati.indexes.rtree.DiskRTree;
import co.geomati.indexes.rtree.RTree;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class RTreeTest extends TestCase {

	private File indexFile;

	private void checkLookUp(RTree tree, Geometry[] data) throws Exception {
		tree.checkTree();
		assertTrue(tree.size() == tree.getAllValues().length);
		Envelope[] keys = tree.getAllValues();
		for (int i = 0; i < keys.length; i++) {
			int[] indexes = tree.getRow(keys[i]);
			assertTrue(contains(indexes, data, keys[i]));
		}

	}

	private boolean contains(int[] indexes, Geometry[] data, Envelope geometry)
			throws Exception {
		for (int i : indexes) {
			if (data[i].getEnvelopeInternal().equals(geometry)) {
				return true;
			}
		}

		return false;
	}

	public void testIndexNGreaterThanBlock() throws Exception {
		testIndexRealData("points", 256, 32, 1000.0);
	}

	public void testIndexPoints() throws Exception {
		testIndexRealData("points", 16, 1024, 1000.0);
	}

	public void testIndexPointsWithSmallN() throws Exception {
		testIndexRealData("points", 3, 32, 1000.0);
	}

	public void testIndexLines() throws Exception {
		testIndexRealData("infra", 16, 1024, 100.0);
	}

	public void testIndexLinesBigN() throws Exception {
		testIndexRealData("infra", 256, 1024, 100.0);
	}

	public void testIndexLinesSmallN() throws Exception {
		testIndexRealData("infra", 3, 1024, 100.0);
	}

	public void testIndexPolygons() throws Exception {
		testIndexRealData("red200k", 16, 1024, 500.0);
	}

	public void testIndexPolygonsBigN() throws Exception {
		testIndexRealData("red200k", 256, 1024, 500.0);
	}

	public void testIndexPolygonsSmallN() throws Exception {
		testIndexRealData("red200k", 3, 1024, 500.0);
	}

	public void testEmptyIndex() throws Exception {
		RTree tree = new DiskRTree(5, 64, false);
		tree.newIndex(indexFile);
		tree.save();
		tree.close();
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 0);
		tree.checkTree();
	}

	public void testIndexWithZeroElements() throws Exception {
		RTree tree = new DiskRTree(5, 64, false);
		tree.newIndex(indexFile);
		assertTrue(tree.size() == 0);
		tree.save();
		tree.close();
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 0);
	}

	public void testRange() throws Exception {
		RTree tree = new DiskRTree(5, 32, false);
		tree.newIndex(indexFile);
		assertTrue(tree.getRange() == null);

		tree.insert(new Envelope(0, 10, 0, 10), 0);
		assertTrue(tree.getRange().getMinX() == 0);
		assertTrue(tree.getRange().getMinY() == 0);
		assertTrue(tree.getRange().getMaxX() == 10);
		assertTrue(tree.getRange().getMaxY() == 10);

		tree.insert(new Envelope(0, 10, -10, 10), 1);
		assertTrue(tree.getRange().getMinX() == 0);
		assertTrue(tree.getRange().getMinY() == -10);
		assertTrue(tree.getRange().getMaxX() == 10);
		assertTrue(tree.getRange().getMaxY() == 10);

		tree.insert(new Envelope(-10, 10, 0, 10), 2);
		assertTrue(tree.getRange().getMinX() == -10);
		assertTrue(tree.getRange().getMinY() == -10);
		assertTrue(tree.getRange().getMaxX() == 10);
		assertTrue(tree.getRange().getMaxY() == 10);

		tree.delete(new Envelope(-10, 10, 0, 10), 2);
		assertTrue(tree.getRange().getMinX() == 0);
		assertTrue(tree.getRange().getMinY() == -10);
		assertTrue(tree.getRange().getMaxX() == 10);
		assertTrue(tree.getRange().getMaxY() == 10);
	}

	public void testNotExistentValues() throws Exception {
		RTree tree = new DiskRTree(5, 32, false);
		tree.newIndex(indexFile);
		// populate the index
		Geometry[] data = read("points");
		for (int i = 0; i < data.length; i++) {
			tree.insert(data[i].getEnvelopeInternal(), i);
		}

		String snapshot = tree.toString();
		tree.delete(data[0].getEnvelopeInternal(), 12412);
		tree.checkTree();
		String snapshot2 = tree.toString();
		assertTrue(snapshot.equals(snapshot2));
		tree.close();
	}

	private void testIndexRealData(String source, int n, int blockSize,
			double checkPeriod) throws Exception {
		RTree tree = new DiskRTree(n, blockSize, false);
		tree.newIndex(indexFile);
		testIndexRealData(source, checkPeriod, tree);
		tree.close();
	}

	private void testIndexRealData(String source, double checkPeriod, RTree tree)
			throws Exception {
		Geometry[] data = read(source);
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < data.length; i++) {
			if (i / (int) checkPeriod == i / checkPeriod) {
				tree.checkTree();
				tree.close();
				tree.openIndex(indexFile);
				tree.checkTree();
				checkLookUp(tree, data);
			}

			tree.insert(data[i].getEnvelopeInternal(), i);
		}
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);
	}

	private Geometry[] read(String source) throws IOException, ParseException {
		ArrayList<Geometry> ret = new ArrayList<Geometry>();

		BufferedReader reader = new BufferedReader(new FileReader(new File(
				"src/test/resources/" + source + ".txt")));
		WKTReader wktReader = new WKTReader();
		String wktGeom;
		while ((wktGeom = reader.readLine()) != null) {
			ret.add(wktReader.read(wktGeom));
		}
		reader.close();

		return ret.toArray(new Geometry[ret.size()]);
	}

	@Override
	protected void setUp() throws Exception {
		indexFile = new File("target/rtreetest.idx");
		if (indexFile.exists()) {
			if (!indexFile.delete()) {
				throw new IOException("Cannot delete the index file");
			}
		}
	}
}
