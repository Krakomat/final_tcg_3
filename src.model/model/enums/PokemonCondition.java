package model.enums;

public enum PokemonCondition {
	POISONED, TOXIC, PARALYZED, HARDEN30, HARDEN20, BLIND, CONFUSED, ASLEEP, DESTINY, NO_DAMAGE, INVULNERABLE, DAMAGEINCREASE10, KNOCKOUT, RETALIATION, POKEMON_POWER_BLOCK, BROCKS_PROTECTION, NO_ENERGY, HALF_DAMAGE, KOGAS_NINJA_TRICK;

	public static boolean isValue(String value) {
		try {
			PokemonCondition.valueOf(value);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
}