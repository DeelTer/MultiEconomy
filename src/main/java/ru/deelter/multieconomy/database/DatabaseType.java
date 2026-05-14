package ru.deelter.multieconomy.database;

import org.jspecify.annotations.NonNull;

public enum DatabaseType {
	MYSQL,
	SQLITE,
	H2;

	public static DatabaseType fromString(@NonNull String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			return MYSQL;
		}
	}
}