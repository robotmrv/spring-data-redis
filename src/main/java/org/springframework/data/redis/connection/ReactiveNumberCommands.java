/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.connection;

import java.nio.ByteBuffer;

import org.reactivestreams.Publisher;
import org.springframework.data.redis.connection.ReactiveRedisConnection.KeyCommand;
import org.springframework.data.redis.connection.ReactiveRedisConnection.NumericResponse;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Redis numeric commands executed using reactive infrastructure.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 2.0
 */
public interface ReactiveNumberCommands {

	/**
	 * Increment value of {@literal key} by 1.
	 *
	 * @param key must not be {@literal null}.
	 * @return
	 */
	default Mono<Long> incr(ByteBuffer key) {

		Assert.notNull(key, "Key must not be null!");

		return incr(Mono.just(new KeyCommand(key))).next().map(NumericResponse::getOutput);
	}

	/**
	 * Increment value of {@literal key} by 1.
	 *
	 * @param keys must not be {@literal null}.
	 * @return
	 */
	Flux<NumericResponse<KeyCommand, Long>> incr(Publisher<KeyCommand> keys);

	/**
	 * {@code INCRBY} command parameters.
	 *
	 * @author Christoph Strobl
	 */
	class IncrByCommand<T extends Number> extends KeyCommand {

		private T value;

		private IncrByCommand(ByteBuffer key, T value) {
			super(key);
			this.value = value;
		}

		/**
		 * Creates a new {@link IncrByCommand} given a {@link ByteBuffer key}.
		 *
		 * @param key must not be {@literal null}.
		 * @return a new {@link IncrByCommand} for {@link ByteBuffer key}.
		 */
		public static <T extends Number> IncrByCommand<T> incr(ByteBuffer key) {

			Assert.notNull(key, "Key must not be null!");

			return new IncrByCommand<T>(key, null);
		}

		/**
		 * Applies the numeric {@literal value}. Constructs a new command instance with all previously configured
		 * properties.
		 *
		 * @param value must not be {@literal null}.
		 * @return a new {@link IncrByCommand} with {@literal value} applied.
		 */
		public IncrByCommand<T> by(T value) {

			Assert.notNull(value, "Value must not be null!");

			return new IncrByCommand<T>(getKey(), value);
		}

		/**
		 * @return
		 */
		public T getValue() {
			return value;
		}
	}

	/**
	 * Increment value of {@literal key} by {@literal value}.
	 *
	 * @param key must not be {@literal null}.
	 * @param value must not be {@literal null}.
	 * @return
	 */
	default <T extends Number> Mono<T> incrBy(ByteBuffer key, T value) {

		Assert.notNull(key, "Key must not be null!");
		Assert.notNull(value, "Value must not be null!");

		return incrBy(Mono.just(IncrByCommand.<T> incr(key).by(value))).next().map(NumericResponse::getOutput);
	}

	/**
	 * Increment value of {@literal key} by {@literal value}.
	 *
	 * @param commands must not be {@literal null}.
	 * @return
	 */
	<T extends Number> Flux<NumericResponse<ReactiveNumberCommands.IncrByCommand<T>, T>> incrBy(
			Publisher<ReactiveNumberCommands.IncrByCommand<T>> commands);

	/**
	 * {@code DECRBY} command parameters.
	 *
	 * @author Christoph Strobl
	 */
	class DecrByCommand<T extends Number> extends KeyCommand {

		private T value;

		private DecrByCommand(ByteBuffer key, T value) {
			super(key);
			this.value = value;
		}

		/**
		 * Creates a new {@link DecrByCommand} given a {@link ByteBuffer key}.
		 *
		 * @param key must not be {@literal null}.
		 * @return a new {@link DecrByCommand} for {@link ByteBuffer key}.
		 */
		public static <T extends Number> DecrByCommand<T> decr(ByteBuffer key) {

			Assert.notNull(key, "Key must not be null!");

			return new DecrByCommand<T>(key, null);
		}

		/**
		 * Applies the numeric {@literal value}. Constructs a new command instance with all previously configured
		 * properties.
		 *
		 * @param value must not be {@literal null}.
		 * @return a new {@link DecrByCommand} with {@literal value} applied.
		 */
		public DecrByCommand<T> by(T value) {

			Assert.notNull(value, "Value must not be null!");

			return new DecrByCommand<T>(getKey(), value);
		}

		/**
		 * @return
		 */
		public T getValue() {
			return value;
		}
	}

	/**
	 * Decrement value of {@literal key} by 1.
	 *
	 * @param key must not be {@literal null}.
	 * @return
	 */
	default Mono<Long> decr(ByteBuffer key) {

		Assert.notNull(key, "Key must not be null!");

		return decr(Mono.just(new KeyCommand(key))).next().map(NumericResponse::getOutput);
	}

	/**
	 * Decrement value of {@literal key} by 1.
	 *
	 * @param keys must not be {@literal null}.
	 * @return
	 */
	Flux<NumericResponse<KeyCommand, Long>> decr(Publisher<KeyCommand> keys);

	/**
	 * Decrement value of {@literal key} by {@literal value}.
	 *
	 * @param key must not be {@literal null}.
	 * @param value must not be {@literal null}.
	 * @return
	 */
	default <T extends Number> Mono<T> decrBy(ByteBuffer key, T value) {

		Assert.notNull(key, "Key must not be null!");
		Assert.notNull(value, "Value must not be null!");

		return decrBy(Mono.just(DecrByCommand.<T> decr(key).by(value))).next().map(NumericResponse::getOutput);
	}

	/**
	 * Decrement value of {@literal key} by {@literal value}.
	 *
	 * @param commands must not be {@literal null}.
	 * @return
	 */
	<T extends Number> Flux<NumericResponse<DecrByCommand<T>, T>> decrBy(Publisher<DecrByCommand<T>> commands);

	/**
	 * {@code HINCRBY} command parameters.
	 *
	 * @author Christoph Strobl
	 */
	class HIncrByCommand<T extends Number> extends KeyCommand {

		private final ByteBuffer field;
		private final T value;

		private HIncrByCommand(ByteBuffer key, ByteBuffer field, T value) {

			super(key);

			this.field = field;
			this.value = value;
		}

		/**
		 * Creates a new {@link HIncrByCommand} given a {@link ByteBuffer key}.
		 *
		 * @param field must not be {@literal null}.
		 * @return a new {@link HIncrByCommand} for {@link ByteBuffer key}.
		 */
		public static <T extends Number> HIncrByCommand<T> incr(ByteBuffer field) {

			Assert.notNull(field, "Field must not be null!");

			return new HIncrByCommand<T>(null, field, null);
		}

		/**
		 * Applies the numeric {@literal value}. Constructs a new command instance with all previously configured
		 * properties.
		 *
		 * @param value must not be {@literal null}.
		 * @return a new {@link HIncrByCommand} with {@literal value} applied.
		 */
		public HIncrByCommand<T> by(T value) {

			Assert.notNull(value, "Value must not be null!");

			return new HIncrByCommand<T>(getKey(), field, value);
		}

		/**
		 * Applies the {@literal key}. Constructs a new command instance with all previously configured properties.
		 *
		 * @param key must not be {@literal null}.
		 * @return a new {@link HIncrByCommand} with {@literal key} applied.
		 */
		public HIncrByCommand<T> forKey(ByteBuffer key) {

			Assert.notNull(key, "Key must not be null!");

			return new HIncrByCommand<T>(key, field, value);
		}

		/**
		 * @return
		 */
		public T getValue() {
			return value;
		}

		/**
		 * @return
		 */
		public ByteBuffer getField() {
			return field;
		}
	}

	/**
	 * Increment {@literal value} of a hash {@literal field} by the given {@literal value}.
	 *
	 * @param key must not be {@literal null}.
	 * @param field must not be {@literal null}.
	 * @param value must not be {@literal null}.
	 * @return
	 */
	default <T extends Number> Mono<T> hIncrBy(ByteBuffer key, ByteBuffer field, T value) {

		Assert.notNull(key, "Key must not be null!");
		Assert.notNull(field, "Field must not be null!");
		Assert.notNull(value, "Value must not be null!");

		return hIncrBy(Mono.just(HIncrByCommand.<T> incr(field).by(value).forKey(key))).next()
				.map(NumericResponse::getOutput);
	}

	/**
	 * Increment {@literal value} of a hash {@literal field} by the given {@literal value}.
	 *
	 * @return
	 */
	<T extends Number> Flux<NumericResponse<HIncrByCommand<T>, T>> hIncrBy(Publisher<HIncrByCommand<T>> commands);

}