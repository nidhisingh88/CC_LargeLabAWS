package main.java.nl.tud.cc.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import main.java.nl.tud.cc.rest.resources.ConverterResource;
import main.java.nl.tud.cc.rest.resources.TestResource;

public class RestApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(TestResource.class);
		s.add(ConverterResource.class);
		return s;
	}
}
