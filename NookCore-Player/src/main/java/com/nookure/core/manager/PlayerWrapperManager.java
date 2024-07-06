package com.nookure.core.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Singleton;
import com.nookure.core.PlayerWrapperBase;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

/**
 * Manages all the player wrappers.
 * <p>
 * You can get the instance of this class by injecting it,
 * see {@link com.google.inject.Injector#getInstance(Class)}
 * <br>
 *
 * @param <T> The player class of the player wrapper implementation
 *            (e.g. PlayerWrapper@Player or PlayerWrapper@ProxiedPlayer>)
 *            This is used to get the player wrapper by its player class.
 *            But you can also use the unique id of the player wrapper.
 *            see {@link #getPlayerWrapper(UUID)}
 * @see PlayerWrapperBase for more information about player wrappers
 * @since 1.0.0
 */
@Singleton
public class PlayerWrapperManager<T, P extends PlayerWrapperBase> {
  private final BiMap<T, P> playerWrappersByPlayerClass = HashBiMap.create();
  private final LinkedHashMap<UUID, P> playerWrappersByUUID = new LinkedHashMap<>();

  /**
   * Gets a player wrapper by its player class.
   *
   * @param player the player class of the player wrapper
   * @return an optional containing the player wrapper if it exists
   */
  @NotNull
  public Optional<PlayerWrapperBase> getPlayerWrapper(@NotNull T player) {
    Objects.requireNonNull(player, "Player cannot be null");
    return Optional.ofNullable(playerWrappersByPlayerClass.get(player));
  }

  /**
   * Gets a player wrapper by its unique id.
   *
   * @param uuid the unique id of the player wrapper
   * @return an optional containing the player wrapper if it exists
   */
  @NotNull
  public Optional<PlayerWrapperBase> getPlayerWrapper(@NotNull UUID uuid) {
    Objects.requireNonNull(uuid, "UUID cannot be null");
    return Optional.ofNullable(playerWrappersByUUID.get(uuid));
  }

  /**
   * Gets a player by its player wrapper.
   *
   * @param playerWrapperBase the player wrapper of the player
   * @return an optional containing the player if it exists
   */
  @NotNull
  public Optional<T> getPlayer(@NotNull P playerWrapperBase) {
    Objects.requireNonNull(playerWrapperBase, "PlayerWrapper cannot be null");
    return Optional.ofNullable(playerWrappersByPlayerClass.inverse().get(playerWrapperBase));
  }

  /**
   * Adds a player wrapper to the manager.
   *
   * @param player            the player that will be used as a key
   * @param playerWrapperBase the player wrapper that will be used as a value
   */
  public void addPlayerWrapper(@NotNull T player, @NotNull P playerWrapperBase) {
    Objects.requireNonNull(player, "Player cannot be null");
    Objects.requireNonNull(playerWrapperBase, "PlayerWrapper cannot be null");

    playerWrappersByPlayerClass.put(player, playerWrapperBase);
    playerWrappersByUUID.put(playerWrapperBase.getUniqueId(), playerWrapperBase);
  }

  /**
   * Removes a player wrapper from the manager.
   *
   * @param player the player that will be used as a key
   */
  public void removePlayerWrapper(@NotNull T player) {
    Objects.requireNonNull(player, "Player cannot be null");

    PlayerWrapperBase playerWrapperBase = playerWrappersByPlayerClass.remove(player);
    playerWrappersByUUID.remove(playerWrapperBase.getUniqueId());
  }

  /**
   * Gets the values as a stream.
   *
   * @return a stream of player wrappers
   */
  public Stream<P> stream() {
    return playerWrappersByUUID.values().stream();
  }

  /**
   * Clears all the mappings from the manager.
   */
  public void clear() {
    playerWrappersByPlayerClass.clear();
    playerWrappersByUUID.clear();
  }
}
