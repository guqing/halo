/* tslint:disable */
/* eslint-disable */
/**
 * Halo Next API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 2.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

// May contain unused imports in some cases
// @ts-ignore
import { License } from './license'
// May contain unused imports in some cases
// @ts-ignore
import { PluginAuthor } from './plugin-author'

/**
 *
 * @export
 * @interface PluginSpec
 */
export interface PluginSpec {
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   */
  displayName?: string
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   */
  version: string
  /**
   *
   * @type {PluginAuthor}
   * @memberof PluginSpec
   */
  author?: PluginAuthor
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   */
  logo?: string
  /**
   *
   * @type {{ [key: string]: string; }}
   * @memberof PluginSpec
   */
  pluginDependencies?: { [key: string]: string }
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   */
  homepage?: string
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   */
  description?: string
  /**
   *
   * @type {Array<License>}
   * @memberof PluginSpec
   */
  license?: Array<License>
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   */
  requires?: string
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   * @deprecated
   */
  pluginClass?: string
  /**
   *
   * @type {boolean}
   * @memberof PluginSpec
   */
  enabled?: boolean
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   */
  settingName?: string
  /**
   *
   * @type {string}
   * @memberof PluginSpec
   */
  configMapName?: string
}
