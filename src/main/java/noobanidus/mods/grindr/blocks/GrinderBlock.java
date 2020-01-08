package noobanidus.mods.grindr.blocks;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import noobanidus.mods.grindr.items.GrindstoneItem;
import noobanidus.mods.grindr.tiles.GrinderTile;
import noobanidus.mods.grindr.util.VoxelUtil;

import javax.annotation.Nullable;

public class GrinderBlock extends AbstractFurnaceBlock {
  public static EnumProperty<GrindstoneType> GRINDSTONE = EnumProperty.create("grindstone", GrindstoneType.class);
  public static DirectionProperty FACING = AbstractFurnaceBlock.FACING;
  public static BooleanProperty ACTIVE = AbstractFurnaceBlock.LIT;

  public static VoxelShape EMPTY_VOXEL = VoxelUtil.multiOr(Block.makeCuboidShape(0, 10, 0, 2, 13, 16), Block.makeCuboidShape(0, 0, 0, 16, 10, 16), Block.makeCuboidShape(2, 10, 0, 14, 13, 2), Block.makeCuboidShape(14, 10, 0, 16, 13, 16), Block.makeCuboidShape(2, 10, 14, 14, 13, 16), Block.makeCuboidShape(6.5, 10, 6.5, 9.5, 12, 9.5));
  public static VoxelShape NORMAL_VOXEL = VoxelUtil.multiOr(Block.makeCuboidShape(0, 10, 0, 2, 13, 16), Block.makeCuboidShape(0, 0, 0, 16, 10, 16), Block.makeCuboidShape(2, 10, 0, 14, 13, 2), Block.makeCuboidShape(14, 10, 0, 16, 13, 16), Block.makeCuboidShape(2, 10, 14, 14, 13, 16), Block.makeCuboidShape(4, 10, 4, 12, 16, 12), Block.makeCuboidShape(6.5, 16, 6.5, 9.5, 18, 9.5));

  public GrinderBlock(Properties p_i48440_1_) {
    super(p_i48440_1_);
    setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(ACTIVE, false).with(GRINDSTONE, GrindstoneType.EMPTY));
  }

  @Override
  protected void interactWith(World world, BlockPos blockPos, PlayerEntity playerEntity) {
    TileEntity te = world.getTileEntity(blockPos);
    if (te instanceof GrinderTile) {
      GrinderTile gte = (GrinderTile) te;
      if (playerEntity.getHeldItemMainhand().getItem() instanceof GrindstoneItem) {
        BlockState state = world.getBlockState(blockPos);
        if (state.get(GRINDSTONE) == GrindstoneType.EMPTY) {
          gte.addGrindstone(playerEntity, playerEntity.getHeldItemMainhand());
          return;
        }
      } else if (playerEntity.getHeldItemMainhand().isEmpty() && playerEntity.isSneaking()) {
        gte.removeGrindstone();
        return;
      }

      playerEntity.openContainer((INamedContainerProvider) te);
    }
  }

  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.get(GRINDSTONE) != GrindstoneType.EMPTY) {
      GrinderTile te = (GrinderTile) worldIn.getTileEntity(pos);
      if (te != null) {
        te.removeGrindstone();
      }
    }
    if (state.getBlock() != newState.getBlock()) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof GrinderTile) {
        InventoryHelper.dropInventoryItems(worldIn, pos, (GrinderTile) tileentity);
        worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
    if (state.get(GRINDSTONE) == GrindstoneType.EMPTY) {
      return EMPTY_VOXEL;
    } else {
      return NORMAL_VOXEL;
    }
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(GRINDSTONE, FACING, ACTIVE);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return new GrinderTile();
  }
}