CREATE OR REPLACE FUNCTION comment_reaction_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.reaction = 'LIKE' THEN
UPDATE comment SET like_count = COALESCE(like_count, 0) + 1 WHERE id = NEW.comment_id;
ELSIF NEW.reaction = 'DISLIKE' THEN
UPDATE comment SET dislike_count = COALESCE(dislike_count, 0) + 1 WHERE id = NEW.comment_id;
END IF;

    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.reaction = 'LIKE' AND NEW.reaction = 'DISLIKE' THEN
UPDATE comment
SET like_count = COALESCE(like_count, 0) - 1,
    dislike_count = COALESCE(dislike_count, 0) + 1
WHERE id = NEW.comment_id;
ELSIF OLD.reaction = 'DISLIKE' AND NEW.reaction = 'LIKE' THEN
UPDATE comment
SET like_count = COALESCE(like_count, 0) + 1,
    dislike_count = COALESCE(dislike_count, 0) - 1
WHERE id = NEW.comment_id;
END IF;

    ELSIF TG_OP = 'DELETE' THEN
        IF OLD.reaction = 'LIKE' THEN
UPDATE comment SET like_count = COALESCE(like_count, 0) - 1 WHERE id = OLD.comment_id;
ELSIF OLD.reaction = 'DISLIKE' THEN
UPDATE comment SET dislike_count = COALESCE(dislike_count, 0) - 1 WHERE id = OLD.comment_id;
END IF;
RETURN OLD;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER comment_reaction_trigger
BEFORE INSERT OR UPDATE OR DELETE
                 ON comment_reaction
                     FOR EACH ROW
                     EXECUTE FUNCTION comment_reaction_function();