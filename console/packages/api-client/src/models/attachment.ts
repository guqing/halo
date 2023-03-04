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
import { AttachmentSpec } from './attachment-spec'
// May contain unused imports in some cases
// @ts-ignore
import { AttachmentStatus } from './attachment-status'
// May contain unused imports in some cases
// @ts-ignore
import { Metadata } from './metadata'

/**
 *
 * @export
 * @interface Attachment
 */
export interface Attachment {
  /**
   *
   * @type {AttachmentSpec}
   * @memberof Attachment
   */
  spec: AttachmentSpec
  /**
   *
   * @type {AttachmentStatus}
   * @memberof Attachment
   */
  status?: AttachmentStatus
  /**
   *
   * @type {string}
   * @memberof Attachment
   */
  apiVersion: string
  /**
   *
   * @type {string}
   * @memberof Attachment
   */
  kind: string
  /**
   *
   * @type {Metadata}
   * @memberof Attachment
   */
  metadata: Metadata
}
