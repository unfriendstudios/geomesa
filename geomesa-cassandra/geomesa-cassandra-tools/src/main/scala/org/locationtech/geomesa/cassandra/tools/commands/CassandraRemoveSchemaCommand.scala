/***********************************************************************
* Copyright (c) 2013-2016 Commonwealth Computer Research, Inc.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Apache License, Version 2.0
* which accompanies this distribution and is available at
* http://www.opensource.org/licenses/apache2.0.php.
*************************************************************************/

package org.locationtech.geomesa.cassandra.tools.commands

import com.beust.jcommander.Parameters
import org.locationtech.geomesa.cassandra.data.CassandraDataStore
import org.locationtech.geomesa.cassandra.tools.{CassandraDataStoreCommand, CassandraDataStoreParams}
import org.locationtech.geomesa.tools.CatalogParam
import org.locationtech.geomesa.tools.data.{RemoveSchemaCommand, RemoveSchemaParams}

class CassandraRemoveSchemaCommand  extends RemoveSchemaCommand[CassandraDataStore] with CassandraDataStoreCommand {
  override val params = new CassandraRemoveSchemaParams
}

@Parameters(commandDescription = "Remove a schema and associated features from a GeoMesa catalog")
class CassandraRemoveSchemaParams extends CassandraDataStoreParams with CatalogParam with RemoveSchemaParams
