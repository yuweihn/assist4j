if redis.call('get', KEYS[1]) == ARGV[1] then
    if redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'XX') == "OK" then
        return ARGV[1];
    else
        return nil;
    end
else
    if redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2], 'NX') == "OK" then
        return ARGV[1];
    else
        return redis.call('get', KEYS[1]);
    end
end