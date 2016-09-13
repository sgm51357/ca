package org.aktin.ca;

import org.glassfish.hk2.utilities.binding.AbstractBinder;


public class MyBinder extends AbstractBinder{
	@Override
	protected void configure() {
//		bind(Impl.class).to(Inter.class);
		// singleton
//
//		bind(new AuthCache(ds)).to(AuthCache.class);
//		bind(new DatabaseBackend(ds)).to(DatabaseBackend.class);
//		bind(new RequestTypeManager()).to(RequestTypeManager.class);

		//bind(PMService.class).to(AbstractCell.class);
		//bind(WorkplaceService.class).to(AbstractCell.class);
	}

}
