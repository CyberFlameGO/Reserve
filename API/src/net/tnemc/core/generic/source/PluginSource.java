package net.tnemc.core.generic.source;

import org.jetbrains.annotations.NotNull;

/**
 * Created by creatorfromhell on 8/9/2017.
 * <p>
 * Reserve API
 * <p>
 * Copyright (C) 2021 creatorfromhell
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA, or see
 * it at <https://www.gnu.org/licenses/lgpl-3.0.txt>.
 **/

/**
 * Represents an {@link ActionSource source} that represents a plugin performing actions.
 *
 * @author creatorfromhell
 * @since 1.0.0
 */
public class PluginSource implements ActionSource {

  /**
   * Represents the name of the plugin that is being used for actions.
   */
  private String plugin;

  /**
   * Represents the reason for actions.
   */
  private String reason = "Default action.";

  /**
   * Used to create an {@link ActionSource source} object that represents a plugin.
   *
   * @param plugin The name of the plugin causing the action.
   *
   * @since 1.0.0
   */
  public PluginSource(String plugin) {
    this.plugin = plugin;
  }

  /**
   * Used to create an {@link ActionSource source} object that represents a plugin with a specific
   * reason attached.
   *
   * @param plugin The name of the plugin causing the action.
   * @param reason The reason the action was performed.
   *
   * @since 1.0.0
   */
  public PluginSource(@NotNull String plugin, @NotNull String reason) {
    this.plugin = plugin;
    this.reason = reason;
  }

  /**
   * Used to change the reason of this {@link PluginSource source}. This could be used to utilize a
   * singular {@link PluginSource source} object while further modifying the reason as needed.
   *
   * @param reason The reason the action was performed.
   *
   * @return The modified {@link PluginSource source} with the specified reason.
   *
   * @since 1.0.0
   */
  public PluginSource withReason(@NotNull String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Used to get the name of the source of the action.
   *
   * @return The name of the source for a specific action. This could be user-friendly
   * or not. This should be the name of the implementation that has caused this action to occur. For
   * instance, a plugin name.
   * <p>
   * Please note: There is no guarantee of uniqueness.
   *
   * @since 1.0.0
   */
  @Override
  public String name() {
    return plugin;
  }

  /**
   * Used to get a description of the reason for why the action was performed.
   *
   * @return The reason for the action that was performed.
   *
   * @since 1.0.0
   */
  @Override
  public String reason() {
    return reason;
  }
}
