if redis.call('get', KEYS[1]) == ARGV[1] then
    redis.call('expire', KEYS[1], ARGV[2]);
    return ARGV[1];
else
    redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'NX');
    return redis.call('get', KEYS[1]);
end

