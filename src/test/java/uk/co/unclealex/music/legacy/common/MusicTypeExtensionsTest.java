/**
 * Copyright 2011 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author unclealex72
 *
 */

package uk.co.unclealex.music.legacy.common;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author alex
 *
 */
public class MusicTypeExtensionsTest {

	@Test
	public void testFlac() {
		new Tester("flac") {
			@Override
			protected MusicType createMusicType(MusicTypeFactory musicTypeFactory) {
				return musicTypeFactory.createFlacType();
			}
		};
	}

	@Test
	public void testMp3() {
		new Tester("mp3") {
			@Override
			protected MusicType createMusicType(MusicTypeFactory musicTypeFactory) {
				return musicTypeFactory.createMp3Type();
			}
		};
	}

	@Test
	public void testOgg() {
		new Tester("ogg") {
			@Override
			protected MusicType createMusicType(MusicTypeFactory musicTypeFactory) {
				return musicTypeFactory.createOggType();
			}
		};
	}

	abstract class Tester {
		
		public Tester(String expectedExtension) {
			MusicTypeFactory musicTypeFactory = new MusicTypeFactoryImpl();
			MusicType musicType = createMusicType(musicTypeFactory);
			String actualExtension = musicType.getExtension();
			Assert.assertEquals("The wrong extension was returned.", expectedExtension, actualExtension);
		}
		
		protected abstract MusicType createMusicType(MusicTypeFactory musicTypeFactory);
	}
}
